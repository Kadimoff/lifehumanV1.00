package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LongevityEmojiCatalog
import com.example.data.db.AppDatabase
import com.example.data.db.LongevityRecordEntity
import com.example.data.db.LongevityRepository
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.BiomarkerProfile
import com.example.data.model.MorningLongevityBriefing
import com.example.data.model.PresetProfile
import com.example.data.model.StroopTrialResult
import com.example.data.model.StroopWordItem
import com.example.data.model.TrajectoryForecast
import com.example.data.model.UserProfile
import com.example.service.GeminiLongevityService
import com.example.service.HealthConnectData
import com.example.service.HealthConnectManager
import com.example.service.HealthConnectStatus
import com.example.service.LongevityCalculatorService
import com.example.service.PdfReportService

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

class LongevityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LongevityRepository
    val healthConnectManager = HealthConnectManager(application)
    private val geminiService = GeminiLongevityService()

    // Health Connect states
    private val _healthConnectStatus = MutableStateFlow(healthConnectManager.checkAvailability())
    val healthConnectStatus: StateFlow<HealthConnectStatus> = _healthConnectStatus.asStateFlow()

    private val _healthConnectData = MutableStateFlow(HealthConnectData())
    val healthConnectData: StateFlow<HealthConnectData> = _healthConnectData.asStateFlow()

    private val _isSyncingHealthConnect = MutableStateFlow(false)
    val isSyncingHealthConnect: StateFlow<Boolean> = _isSyncingHealthConnect.asStateFlow()

    private val _healthPermissionsGranted = MutableStateFlow(false)
    val healthPermissionsGranted: StateFlow<Boolean> = _healthPermissionsGranted.asStateFlow()

    private val _healthSyncFeedbackMessage = MutableStateFlow<String?>(null)
    val healthSyncFeedbackMessage: StateFlow<String?> = _healthSyncFeedbackMessage.asStateFlow()

    // AI Morning Briefing states
    private val _morningBriefing = MutableStateFlow<MorningLongevityBriefing?>(null)
    val morningBriefing: StateFlow<MorningLongevityBriefing?> = _morningBriefing.asStateFlow()

    private val _isGeneratingBriefing = MutableStateFlow(false)
    val isGeneratingBriefing: StateFlow<Boolean> = _isGeneratingBriefing.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = LongevityRepository(db.longevityRecordDao())
    }


    fun checkHealthPermissions() {
        viewModelScope.launch {
            _healthConnectStatus.value = healthConnectManager.checkAvailability()
            val granted = healthConnectManager.hasAllPermissions()
            _healthPermissionsGranted.value = granted
            if (granted) {
                syncHealthConnectData()
            }
        }
    }

    /**
     * Fetch live heart rate & physical activity metrics from Health Connect
     */
    fun syncHealthConnectData() {
        viewModelScope.launch {
            _isSyncingHealthConnect.value = true
            _healthSyncFeedbackMessage.value = "Connecting to Health Connect sensor database..."
            delay(350)
            val data = healthConnectManager.fetchHealthData()
            _healthConnectData.value = data
            _isSyncingHealthConnect.value = false

            if (data.isLiveSynced) {
                applyHealthConnectTelemetry(data)
                _healthSyncFeedbackMessage.value = "Synced! Resting HR: ${data.restingHeartRateBpm?.toInt() ?: "--"} bpm • Steps: ${data.totalStepsToday} • Active: ${data.activeCaloriesKcal.toInt()} kcal"
                triggerHapticFeedback(40)
            } else {
                _healthSyncFeedbackMessage.value = "Ready to connect. Tap 'Request Permissions' or run live telemetry sync."
            }
        }
    }

    /**
     * Apply fetched Health Connect telemetry into the Digital Twin BiomarkerProfile
     */
    fun applyHealthConnectTelemetry(data: HealthConnectData) {
        val current = _biomarkerProfile.value
        val updatedRhr = data.restingHeartRateBpm ?: current.restingPulseBpm
        val updatedSteps = if (data.totalStepsToday > 0) data.totalStepsToday.toInt() else current.dailySteps
        val updatedActiveCalories = if (data.activeCaloriesKcal > 0) data.activeCaloriesKcal else current.activeCaloriesKcal
        val updatedExerciseMinutes = if (data.exerciseMinutesToday > 0) data.exerciseMinutesToday else current.exerciseMinutesToday

        val updatedProfile = current.copy(
            restingPulseBpm = (updatedRhr * 10.0).toInt() / 10.0,
            dailySteps = updatedSteps,
            activeCaloriesKcal = (updatedActiveCalories * 10.0).toInt() / 10.0,
            exerciseMinutesToday = updatedExerciseMinutes,
            isHealthConnectSynced = true,
            healthConnectSyncTimestamp = data.syncTimestamp
        )
        updateProfile(updatedProfile)
    }

    /**
     * Manual or Demo Telemetry Simulation (useful for testing on emulator or when HC data provider isn't populated)
     */
    fun simulateDeviceHealthTelemetry(
        pulseBpm: Double = 56.0,
        steps: Long = 10840L,
        activeKcal: Double = 460.0,
        exerciseMins: Int = 45
    ) {
        val simulated = HealthConnectData(
            restingHeartRateBpm = pulseBpm,
            avgHeartRateBpm = pulseBpm + 14.0,
            minHeartRateBpm = pulseBpm - 6.0,
            maxHeartRateBpm = 145.0,
            totalStepsToday = steps,
            activeCaloriesKcal = activeKcal,
            totalCaloriesKcal = 2150.0,
            distanceMeters = (steps * 0.78),
            exerciseMinutesToday = exerciseMins,
            exerciseSessionCount = 1,
            isLiveSynced = true,
            syncTimestamp = System.currentTimeMillis(),
            sourceSummary = "Health Connect Device Pipeline (Live)"
        )
        _healthConnectData.value = simulated
        _healthPermissionsGranted.value = true
        applyHealthConnectTelemetry(simulated)
        _healthSyncFeedbackMessage.value = "Device data synced! Resting HR: ${pulseBpm.toInt()} bpm • Steps: $steps • Reserves recalculated!"
        triggerHapticFeedback(50)
    }

    fun clearFeedbackMessage() {
        _healthSyncFeedbackMessage.value = null
    }

    val historyRecords: StateFlow<List<LongevityRecordEntity>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Theme State (Dark / Light mode)
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        triggerHapticFeedback(25)
    }

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
        triggerHapticFeedback(20)
    }

    // User Profile & Multi-Account States
    private val _allUsers = MutableStateFlow(LongevityEmojiCatalog.sampleUsers)
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    private val _currentUserProfile = MutableStateFlow(LongevityEmojiCatalog.sampleUsers.first())
    val currentUserProfile: StateFlow<UserProfile> = _currentUserProfile.asStateFlow()

    // Giriş Menüsü (Entry / Welcome / Login Menu) State
    private val _isEntryMenuOpen = MutableStateFlow(false)
    val isEntryMenuOpen: StateFlow<Boolean> = _isEntryMenuOpen.asStateFlow()

    fun openEntryMenu() {
        _isEntryMenuOpen.value = true
        triggerHapticFeedback(30)
    }

    fun closeEntryMenu() {
        _isEntryMenuOpen.value = false
    }

    fun switchUser(userId: String) {
        val found = _allUsers.value.find { it.id == userId } ?: return
        _currentUserProfile.value = found
        // Synchronize biological age baseline with profile
        val currentBio = _biomarkerProfile.value
        if (currentBio.chronologicalAge != found.chronologicalAge) {
            updateProfile(currentBio.copy(
                chronologicalAge = found.chronologicalAge,
                isMale = found.biologicalSex.equals("Male", ignoreCase = true),
                heightCm = found.heightCm,
                weightKg = found.weightKg
            ))
        }
        triggerHapticFeedback(40)
    }

    fun updateUserProfile(updated: UserProfile) {
        _currentUserProfile.value = updated
        _allUsers.value = _allUsers.value.map { if (it.id == updated.id) updated else it }
        val currentBio = _biomarkerProfile.value
        updateProfile(currentBio.copy(
            chronologicalAge = updated.chronologicalAge,
            isMale = updated.biologicalSex.equals("Male", ignoreCase = true),
            heightCm = updated.heightCm,
            weightKg = updated.weightKg
        ))
        triggerHapticFeedback(30)
    }

    fun selectAvatarEmoji(emoji: String, badgeTitle: String) {
        val current = _currentUserProfile.value
        val updated = current.copy(
            avatarEmoji = emoji,
            avatarBadgeTitle = badgeTitle
        )
        updateUserProfile(updated)
    }

    fun addNewUser(newUser: UserProfile) {
        _allUsers.value = _allUsers.value + newUser
        _currentUserProfile.value = newUser
        triggerHapticFeedback(50)
    }

    // 1. Core Profile & Computed Twin State
    private val _biomarkerProfile = MutableStateFlow(BiomarkerProfile())
    val biomarkerProfile: StateFlow<BiomarkerProfile> = _biomarkerProfile.asStateFlow()

    private val _twinResult = MutableStateFlow(LongevityCalculatorService.computeDigitalTwin(_biomarkerProfile.value))
    val twinResult: StateFlow<BiologicalTwinResult> = _twinResult.asStateFlow()

    private val _trajectoryForecast = MutableStateFlow(
        LongevityCalculatorService.forecastTrajectories(_biomarkerProfile.value, _twinResult.value)
    )
    val trajectoryForecast: StateFlow<TrajectoryForecast> = _trajectoryForecast.asStateFlow()

    // 2. Trajectory Year Scrubbing State
    private val _selectedScrubYear = MutableStateFlow(5)
    val selectedScrubYear: StateFlow<Int> = _selectedScrubYear.asStateFlow()

    // 3. Preset Longevity Profiles
    val presetProfiles = listOf(
        PresetProfile(
            id = "super_ager",
            title = "Optimal Super-Ager",
            subtitle = "Low inflammation, high VO2max & insulin sensitivity",
            tag = "BioAge: -8.4 yrs",
            profile = BiomarkerProfile(
                chronologicalAge = 48.0,
                glucoseMgDl = 84.0,
                insulinUuMl = 3.5,
                hsCrpMgL = 0.25,
                albuminGDl = 4.7,
                creatinineUmolL = 72.0,
                lymphocytePercent = 35.0,
                mcvFl = 87.0,
                rdwPercent = 11.8,
                alpUL = 52.0,
                wbc103Ul = 4.9,
                platelets103Ul = 220.0,
                neutrophilsPercent = 54.0,
                sbpMmHg = 110.0,
                dbpMmHg = 70.0,
                restingPulseBpm = 52.0,
                totalCholesterolMgDl = 165.0,
                hdlMgDl = 68.0,
                triglyceridesMgDl = 65.0,
                heightCm = 178.0,
                weightKg = 70.0,
                waistCircumferenceCm = 76.0,
                testosteroneNgDl = 720.0,
                dheaSUgDl = 340.0,
                tshUiuMl = 1.6,
                vitaminDNgMl = 64.0,
                chairStand30sReps = 24,
                walkingSpeed4mMps = 1.45,
                stroopMeanLatencyMs = 460.0,
                stroopAccuracyPercent = 100.0,
                phq9Score = 1,
                gad7Score = 1,
                psqiSleepScore = 2
            )
        ),
        PresetProfile(
            id = "population_median",
            title = "Population Median",
            subtitle = "Average Western clinical reference standard",
            tag = "BioAge: +0.2 yrs",
            profile = BiomarkerProfile()
        ),
        PresetProfile(
            id = "cardiometabolic",
            title = "Cardiometabolic Stress",
            subtitle = "Elevated HOMA-IR, arterial stiffness & low muscle",
            tag = "BioAge: +6.8 yrs",
            profile = BiomarkerProfile(
                chronologicalAge = 45.0,
                glucoseMgDl = 112.0,
                insulinUuMl = 16.0,
                hsCrpMgL = 2.8,
                albuminGDl = 4.1,
                creatinineUmolL = 88.0,
                lymphocytePercent = 26.0,
                mcvFl = 91.0,
                rdwPercent = 13.9,
                alpUL = 85.0,
                wbc103Ul = 7.8,
                platelets103Ul = 290.0,
                neutrophilsPercent = 68.0,
                sbpMmHg = 138.0,
                dbpMmHg = 88.0,
                restingPulseBpm = 78.0,
                totalCholesterolMgDl = 230.0,
                hdlMgDl = 42.0,
                triglyceridesMgDl = 195.0,
                heightCm = 175.0,
                weightKg = 88.0,
                waistCircumferenceCm = 96.0,
                testosteroneNgDl = 380.0,
                dheaSUgDl = 160.0,
                tshUiuMl = 3.2,
                vitaminDNgMl = 24.0,
                chairStand30sReps = 11,
                walkingSpeed4mMps = 1.05,
                stroopMeanLatencyMs = 690.0,
                stroopAccuracyPercent = 92.0,
                phq9Score = 6,
                gad7Score = 5,
                psqiSleepScore = 8
            )
        ),
        PresetProfile(
            id = "inflammaging",
            title = "High Inflammaging Burden",
            subtitle = "Elevated hs-CRP, high SII & immune senescence",
            tag = "BioAge: +5.4 yrs",
            profile = BiomarkerProfile(
                chronologicalAge = 50.0,
                glucoseMgDl = 98.0,
                insulinUuMl = 8.5,
                hsCrpMgL = 4.2,
                albuminGDl = 3.9,
                creatinineUmolL = 92.0,
                lymphocytePercent = 21.0,
                mcvFl = 93.0,
                rdwPercent = 14.8,
                alpUL = 92.0,
                wbc103Ul = 8.9,
                platelets103Ul = 340.0,
                neutrophilsPercent = 74.0,
                sbpMmHg = 126.0,
                dbpMmHg = 80.0,
                restingPulseBpm = 72.0,
                totalCholesterolMgDl = 205.0,
                hdlMgDl = 46.0,
                triglyceridesMgDl = 140.0,
                heightCm = 172.0,
                weightKg = 76.0,
                waistCircumferenceCm = 88.0,
                testosteroneNgDl = 450.0,
                dheaSUgDl = 180.0,
                tshUiuMl = 2.4,
                vitaminDNgMl = 29.0,
                chairStand30sReps = 14,
                walkingSpeed4mMps = 1.15,
                stroopMeanLatencyMs = 620.0,
                stroopAccuracyPercent = 94.0,
                phq9Score = 4,
                gad7Score = 4,
                psqiSleepScore = 6
            )
        )
    )

    init {
        checkHealthPermissions()
        refreshMorningBriefing(forceAi = false)
    }

    // 4. Update Profile Logic
    fun updateProfile(newProfile: BiomarkerProfile) {
        _biomarkerProfile.value = newProfile
        val newTwin = LongevityCalculatorService.computeDigitalTwin(newProfile)
        _twinResult.value = newTwin
        _trajectoryForecast.value = LongevityCalculatorService.forecastTrajectories(newProfile, newTwin)
        refreshMorningBriefing(forceAi = false)
    }

    fun refreshMorningBriefing(forceAi: Boolean = true) {
        viewModelScope.launch {
            _isGeneratingBriefing.value = true
            try {
                if (forceAi) {
                    val briefing = geminiService.generateMorningBriefing(
                        _biomarkerProfile.value,
                        _twinResult.value,
                        _healthConnectData.value
                    )
                    _morningBriefing.value = briefing
                } else {
                    val fallback = geminiService.generateDeterministicClinicalBriefing(
                        _biomarkerProfile.value,
                        _twinResult.value,
                        _healthConnectData.value
                    )
                    _morningBriefing.value = fallback
                }
            } catch (e: Exception) {
                _morningBriefing.value = geminiService.generateDeterministicClinicalBriefing(
                    _biomarkerProfile.value,
                    _twinResult.value,
                    _healthConnectData.value
                )
            } finally {
                _isGeneratingBriefing.value = false
            }
        }
    }


    fun applyPreset(presetId: String) {
        val preset = presetProfiles.find { it.id == presetId } ?: return
        updateProfile(preset.profile)
    }

    fun setSelectedScrubYear(year: Int) {
        _selectedScrubYear.value = year
    }

    fun saveCurrentAssessment(label: String = "Assessment") {
        viewModelScope.launch {
            repository.saveAssessment(_biomarkerProfile.value, _twinResult.value, label)
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteRecord(id)
        }
    }

    // 5. Interactive Stroop Test State
    private val colorPalette = listOf(
        Pair("RED", 0xFFFF3B30),
        Pair("BLUE", 0xFF007AFF),
        Pair("GREEN", 0xFF34C759),
        Pair("YELLOW", 0xFFFFCC00),
        Pair("PURPLE", 0xFFAF52DE)
    )

    private val _stroopActive = MutableStateFlow(false)
    val stroopActive: StateFlow<Boolean> = _stroopActive.asStateFlow()

    private val _stroopFinished = MutableStateFlow(false)
    val stroopFinished: StateFlow<Boolean> = _stroopFinished.asStateFlow()

    private val _stroopCurrentIndex = MutableStateFlow(0)
    val stroopCurrentIndex: StateFlow<Int> = _stroopCurrentIndex.asStateFlow()

    private val _stroopStimulus = MutableStateFlow<StroopWordItem?>(null)
    val stroopStimulus: StateFlow<StroopWordItem?> = _stroopStimulus.asStateFlow()

    private val _stroopResults = MutableStateFlow<List<StroopTrialResult>>(emptyList())
    val stroopResults: StateFlow<List<StroopTrialResult>> = _stroopResults.asStateFlow()

    private var trialStartTime: Long = 0L
    private val totalTrials = 12

    fun startStroopTest() {
        _stroopResults.value = emptyList()
        _stroopCurrentIndex.value = 0
        _stroopFinished.value = false
        _stroopActive.value = true
        nextStroopTrial()
    }

    private fun nextStroopTrial() {
        val wordIdx = Random.nextInt(colorPalette.size)
        // Make 65% of trials incongruent for cognitive conflict
        val isIncongruent = Random.nextFloat() < 0.65
        val colorIdx = if (isIncongruent) {
            val otherIndices = (colorPalette.indices).filter { it != wordIdx }
            otherIndices[Random.nextInt(otherIndices.size)]
        } else {
            wordIdx
        }

        _stroopStimulus.value = StroopWordItem(
            displayedWord = colorPalette[wordIdx].first,
            inkColorHex = colorPalette[colorIdx].second,
            inkColorName = colorPalette[colorIdx].first,
            correctColorIndex = colorIdx
        )
        trialStartTime = System.currentTimeMillis()
    }

    fun submitStroopAnswer(chosenColorIndex: Int) {
        if (!_stroopActive.value) return
        val currentStimulus = _stroopStimulus.value ?: return
        val latency = System.currentTimeMillis() - trialStartTime
        val isCorrect = chosenColorIndex == currentStimulus.correctColorIndex

        triggerHapticFeedback(if (isCorrect) 30L else 120L)

        val newResults = _stroopResults.value + StroopTrialResult(
            stimulusIndex = _stroopCurrentIndex.value,
            latencyMs = latency,
            isCorrect = isCorrect
        )
        _stroopResults.value = newResults

        val nextIdx = _stroopCurrentIndex.value + 1
        if (nextIdx < totalTrials) {
            _stroopCurrentIndex.value = nextIdx
            nextStroopTrial()
        } else {
            // Finish test & calculate cognitive reserve updates
            _stroopActive.value = false
            _stroopFinished.value = true
            val correctTrials = newResults.filter { it.isCorrect }
            val meanLatency = if (correctTrials.isNotEmpty()) {
                correctTrials.map { it.latencyMs }.average()
            } else 800.0
            val accuracy = (correctTrials.size.toDouble() / newResults.size.toDouble()) * 100.0

            // Apply directly into profile
            updateProfile(
                _biomarkerProfile.value.copy(
                    stroopMeanLatencyMs = (meanLatency * 10.0).toInt() / 10.0,
                    stroopAccuracyPercent = (accuracy * 10.0).toInt() / 10.0
                )
            )
        }
    }

    // 6. 30-Second Chair Stand Test State
    private val _chairTimerActive = MutableStateFlow(false)
    val chairTimerActive: StateFlow<Boolean> = _chairTimerActive.asStateFlow()

    private val _chairSecondsLeft = MutableStateFlow(30)
    val chairSecondsLeft: StateFlow<Int> = _chairSecondsLeft.asStateFlow()

    private val _chairRepCount = MutableStateFlow(0)
    val chairRepCount: StateFlow<Int> = _chairRepCount.asStateFlow()

    private val _chairFinished = MutableStateFlow(false)
    val chairFinished: StateFlow<Boolean> = _chairFinished.asStateFlow()

    private var chairTimerJob: Job? = null

    fun startChairStandTest() {
        chairTimerJob?.cancel()
        _chairSecondsLeft.value = 30
        _chairRepCount.value = 0
        _chairFinished.value = false
        _chairTimerActive.value = true

        chairTimerJob = viewModelScope.launch {
            while (_chairSecondsLeft.value > 0) {
                delay(1000)
                _chairSecondsLeft.value = _chairSecondsLeft.value - 1
                if (_chairSecondsLeft.value <= 3) {
                    triggerHapticFeedback(50)
                }
            }
            // Finished
            _chairTimerActive.value = false
            _chairFinished.value = true
            triggerHapticFeedback(200)

            // Update Muscle Reserve in BiomarkerProfile
            updateProfile(
                _biomarkerProfile.value.copy(chairStand30sReps = _chairRepCount.value)
            )
        }
    }

    fun incrementChairRep() {
        if (_chairTimerActive.value) {
            _chairRepCount.value = _chairRepCount.value + 1
            triggerHapticFeedback(25)
        }
    }

    fun resetChairTest() {
        chairTimerJob?.cancel()
        _chairTimerActive.value = false
        _chairSecondsLeft.value = 30
        _chairRepCount.value = 0
        _chairFinished.value = false
    }

    // 7. PDF Report Export
    private val _generatedPdfFile = MutableStateFlow<File?>(null)
    val generatedPdfFile: StateFlow<File?> = _generatedPdfFile.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf.asStateFlow()

    fun generatePdfReport(context: Context) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            delay(400) // smooth ui feel
            val file = PdfReportService.generateLongevityReport(
                context,
                _biomarkerProfile.value,
                _twinResult.value,
                _trajectoryForecast.value
            )
            _generatedPdfFile.value = file
            _isGeneratingPdf.value = false
        }
    }

    private fun triggerHapticFeedback(durationMs: Long) {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }
}
