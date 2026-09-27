package com.example.data

import com.example.data.model.LongevityBadge
import com.example.data.model.LongevityEmojiItem
import com.example.data.model.UserProfile

object LongevityEmojiCatalog {

    val allEmojis: List<LongevityEmojiItem> = listOf(
        LongevityEmojiItem(
            id = "dna_epigenetics",
            emoji = "🧬",
            title = "Epigenetic Rejuvenator",
            titleTr = "Epigenetik Gençleşme",
            category = "Cellular",
            categoryTr = "Hücresel",
            meaning = "DNA methylation clock reversal & telomere maintenance",
            meaningTr = "DNA metilasyon saati gençleşmesi ve telomer koruma"
        ),
        LongevityEmojiItem(
            id = "vascular_heart",
            emoji = "🫀",
            title = "Vascular Prodigy",
            titleTr = "Kardiyovasküler Zirve",
            category = "Cardio",
            categoryTr = "Kardiyovasküler",
            meaning = "Endothelial nitric oxide surge & low arterial stiffness",
            meaningTr = "Endotel nitrik oksit üretimi ve esnek damar sağlığı"
        ),
        LongevityEmojiItem(
            id = "mitochondrial_power",
            emoji = "⚡",
            title = "Metabolic Dynamo",
            titleTr = "Metabolik Dinamo",
            category = "Metabolism",
            categoryTr = "Metabolizma",
            meaning = "High insulin sensitivity & mitochondrial biogenesis",
            meaningTr = "Yüksek insülin duyarlılığı ve mitokondriyal enerji"
        ),
        LongevityEmojiItem(
            id = "neuro_brain",
            emoji = "🧠",
            title = "Neuroplastic Master",
            titleTr = "Nöroplastisite Ustası",
            category = "Cognitive",
            categoryTr = "Bilişsel",
            meaning = "Prefrontal cortex processing speed & executive reserve",
            meaningTr = "Prefrontal korteks işlem hızı ve bilişsel rezerv"
        ),
        LongevityEmojiItem(
            id = "immune_shield",
            emoji = "🛡️",
            title = "Inflammaging Sentinel",
            titleTr = "İnflamaging Kalkanı",
            category = "Immune",
            categoryTr = "Bağışıklık",
            meaning = "hs-CRP suppression & low systemic immune inflammation",
            meaningTr = "Düşük hs-CRP ve sistemik steril inflamasyon engelleme"
        ),
        LongevityEmojiItem(
            id = "muscle_sarcopenia",
            emoji = "🏋️",
            title = "Sarcopenia Armor",
            titleTr = "Sarkopeni Savunması",
            category = "Physical",
            categoryTr = "Fiziksel",
            meaning = "Type-II muscle fiber recruitment & neuromuscular power",
            meaningTr = "Hızlı kasılan Tip-II lifler ve nöromusküler güç"
        ),
        LongevityEmojiItem(
            id = "circadian_zen",
            emoji = "🌿",
            title = "Circadian Restorer",
            titleTr = "Sirkadiyen Zen",
            category = "Recovery",
            categoryTr = "Yenilenme",
            meaning = "Deep restorative slow-wave sleep & glymphatic clearance",
            meaningTr = "Derin onarıcı yavaş dalga uykusu ve glimfatik temizlik"
        ),
        LongevityEmojiItem(
            id = "time_reversal",
            emoji = "⏳",
            title = "Biological Chrono-Bender",
            titleTr = "Zaman Bükücü",
            category = "Biological",
            categoryTr = "Biyolojik",
            meaning = "Biological age tracking below chronological calendar age",
            meaningTr = "Biyolojik yaşın takvim yaşından daha genç olması"
        ),
        LongevityEmojiItem(
            id = "grandmaster_trophy",
            emoji = "🏆",
            title = "Longevity Grandmaster",
            titleTr = "Longevity Şampiyonu",
            category = "Milestone",
            categoryTr = "Başarı",
            meaning = "All 7 functional organ reserves scored > 80/100",
            meaningTr = "7 temel organ rezervinin tamamında 80+ puan başarısı"
        ),
        LongevityEmojiItem(
            id = "diamond_biomarkers",
            emoji = "💎",
            title = "Diamond Biomarkers",
            titleTr = "Elmas Biyomarker",
            category = "Clinical",
            categoryTr = "Klinik",
            meaning = "All blood biomarkers within optimal gerontological ranges",
            meaningTr = "Tüm kan biyomarkerlarının optimum aralıkta olması"
        ),
        LongevityEmojiItem(
            id = "autophagy_nutrition",
            emoji = "🥗",
            title = "Cellular Autophagy",
            titleTr = "Hücresel Otofaji",
            category = "Nutrition",
            categoryTr = "Beslenme",
            meaning = "Polyphenol nutrient sensing & cellular debris clearing",
            meaningTr = "Polifenol zengini beslenme ve hücresel atık temizliği"
        ),
        LongevityEmojiItem(
            id = "hormesis_fire",
            emoji = "🔥",
            title = "Hormetic Spark",
            titleTr = "Hormez Ateşi",
            category = "Adaptation",
            categoryTr = "Adaptasyon",
            meaning = "Heat shock proteins & mitochondrial renewal",
            meaningTr = "Isı şoku proteinleri ve hücresel dayanıklılık"
        ),
        LongevityEmojiItem(
            id = "vagus_nerve",
            emoji = "🧘",
            title = "Vagal Harmony",
            titleTr = "Vagus Dengesi",
            category = "Stress",
            categoryTr = "Stres",
            meaning = "Parasympathetic tone & heart rate variability mastery",
            meaningTr = "Parasempatik sinir sistemi ve kalp atım hızı değişkenliği"
        ),
        LongevityEmojiItem(
            id = "cryo_adaptation",
            emoji = "🧊",
            title = "Cold Hormesis",
            titleTr = "Soğuk Hormezi",
            category = "Adaptation",
            categoryTr = "Adaptasyon",
            meaning = "Brown adipose tissue activation & metabolic jumpstart",
            meaningTr = "Kahverengi yağ dokusu aktivasyonu ve metabolizma"
        ),
        LongevityEmojiItem(
            id = "target_precision",
            emoji = "🎯",
            title = "Protocol Sniper",
            titleTr = "Biyohedef Vurucu",
            category = "Precision",
            categoryTr = "Hassasiyet",
            meaning = "Daily actionable protocol completion streak",
            meaningTr = "Günlük klinik longevity protokollerini kesintisiz tamamlama"
        ),
        LongevityEmojiItem(
            id = "centenarian_orbit",
            emoji = "🚀",
            title = "Centenarian Orbit",
            titleTr = "100+ Yaş Yolcusu",
            category = "Vision",
            categoryTr = "Vizyon",
            meaning = "90+ projected functional independence lifespan years",
            meaningTr = "90+ yıl bağımsız ve sağlıklı yaşam beklentisi"
        )
    )

    val achievementBadges: List<LongevityBadge> = listOf(
        LongevityBadge(
            id = "badge_bio_younger",
            emoji = "⏳",
            title = "Age Reversal Pioneer",
            titleTr = "Biyolojik Gençleşme Öncüsü",
            description = "Levine PhenoAge is lower than Chronological Age",
            descriptionTr = "Biyolojik yaşınız takvim yaşınızdan daha genç",
            requirement = "PhenoAge < ChronoAge",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_vascular_shield",
            emoji = "🫀",
            title = "Endothelial Elite",
            titleTr = "Endotel Eliti",
            description = "Resting pulse < 60 bpm and SBP < 120 mmHg",
            descriptionTr = "Dinlenme nabzı < 60 bpm ve tansiyon < 120 mmHg",
            requirement = "Resting HR < 60 bpm",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_metabolic_fire",
            emoji = "⚡",
            title = "Insulin Sensitivity Master",
            titleTr = "İnsülin Duyarlılığı Ustası",
            description = "HOMA-IR index < 1.4 for optimal glycemic control",
            descriptionTr = "Optimum glisemik kontrol için HOMA-IR < 1.4",
            requirement = "HOMA-IR < 1.4",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_inflammaging_zero",
            emoji = "🛡️",
            title = "Inflammaging Barrier",
            titleTr = "İnflamaging Bariyeri",
            description = "High-sensitivity CRP < 0.5 mg/L",
            descriptionTr = "Ultra duyarlı CRP < 0.5 mg/L düzeyinde",
            requirement = "hs-CRP < 0.5 mg/L",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_cognitive_speed",
            emoji = "🧠",
            title = "Synaptic Velocity",
            titleTr = "Sinaptik Hız",
            description = "Stroop reaction test latency under 550 ms",
            descriptionTr = "Stroop renk testi tepki süresi 550 ms altında",
            requirement = "Stroop Latency < 550ms",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_muscle_power",
            emoji = "🏋️",
            title = "Sarcopenia Destroyer",
            titleTr = "Sarkopeni Avcısı",
            description = "20+ reps in the 30-Second Chair Stand test",
            descriptionTr = "30 Saniyelik Sandalye Testinde 20+ tekrar",
            requirement = "Chair Stand > 20 reps",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_wearable_synced",
            emoji = "💎",
            title = "Health Connect Synchronized",
            titleTr = "Health Connect Senkron",
            description = "Live wearable telemetry actively feeding the digital twin",
            descriptionTr = "Giyilebilir cihaz verisi dijital ikizi canlı besliyor",
            requirement = "Wearable Synced",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_streak_master",
            emoji = "🔥",
            title = "28-Day Protocol Streak",
            titleTr = "28 Günlük Protokol Serisi",
            description = "Continuous longevity lifestyle adherence for 4 weeks",
            descriptionTr = "4 hafta boyunca kesintisiz longevity disiplini",
            requirement = "28-Day Streak",
            isUnlocked = true
        )
    )

    // Preset User Personas for fast login / persona switching
    val sampleUsers: List<UserProfile> = listOf(
        UserProfile(
            id = "user_dr_vance",
            name = "Dr. Alexander Vance",
            title = "Longevity Biohacker",
            email = "alexander.vance@lifemap.ai",
            avatarEmoji = "🧬",
            avatarBadgeTitle = "Epigenetic Pioneer",
            chronologicalAge = 42.0,
            biologicalSex = "Male",
            bloodType = "O (Rh+)",
            heightCm = 178.0,
            weightKg = 71.0,
            targetPhenoAgeDelta = -6.0,
            primaryLongevityGoal = "Reverse Biological Age by 6 Years & Maximize VO2Max",
            dailyStepGoal = 10000,
            activeStreakDays = 28,
            bio = "Focused on DNA methylation clock deceleration, intermittent fasting, and Zone 2 mitochondrial biogenesis.",
            unlockedBadges = listOf("🧬", "🫀", "⚡", "🧠", "🛡️", "🏆", "💎", "🔥")
        ),
        UserProfile(
            id = "user_elena",
            name = "Elena Rostova",
            title = "Super-Ager Athlete",
            email = "elena.rostova@lifemap.ai",
            avatarEmoji = "⚡",
            avatarBadgeTitle = "Metabolic Dynamo",
            chronologicalAge = 38.0,
            biologicalSex = "Female",
            bloodType = "A (Rh+)",
            heightCm = 170.0,
            weightKg = 58.0,
            targetPhenoAgeDelta = -7.5,
            primaryLongevityGoal = "Endothelial Elasticity & Peak Neuro-Muscular Power",
            dailyStepGoal = 12000,
            activeStreakDays = 45,
            bio = "Mastering cold water immersion, HRV optimization, and targeted antioxidant polyphenol timing.",
            unlockedBadges = listOf("⚡", "🫀", "🏋️", "🌿", "💎", "⏳", "🏆")
        ),
        UserProfile(
            id = "user_marcus",
            name = "Marcus Chen",
            title = "Cardiometabolic Optimizer",
            email = "marcus.chen@lifemap.ai",
            avatarEmoji = "🫀",
            avatarBadgeTitle = "Vascular Prodigy",
            chronologicalAge = 51.0,
            biologicalSex = "Male",
            bloodType = "B (Rh+)",
            heightCm = 182.0,
            weightKg = 82.0,
            targetPhenoAgeDelta = -4.0,
            primaryLongevityGoal = "Lower Arterial Stiffness & Normalize HOMA-IR",
            dailyStepGoal = 9000,
            activeStreakDays = 14,
            bio = "Reversing metabolic syndrome through carbohydrate timing, nitric oxide precursors, and resistance training.",
            unlockedBadges = listOf("🫀", "🛡️", "🎯", "🌿")
        ),
        UserProfile(
            id = "user_guest",
            name = "Misafir Kullanıcı (Guest)",
            title = "Longevity Explorer",
            email = "guest@lifemap.ai",
            avatarEmoji = "🚀",
            avatarBadgeTitle = "Centenarian Orbit",
            chronologicalAge = 40.0,
            biologicalSex = "Male",
            bloodType = "O (Rh-)",
            heightCm = 175.0,
            weightKg = 75.0,
            targetPhenoAgeDelta = -5.0,
            primaryLongevityGoal = "Explore Human Longevity Digital Twin Sandbox",
            dailyStepGoal = 8000,
            activeStreakDays = 3,
            bio = "Exploring precision aging mathematics and 8 functional organ reserve simulators.",
            unlockedBadges = listOf("🚀", "🧬", "⏳")
        )
    )
}
