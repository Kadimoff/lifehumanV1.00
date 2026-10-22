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
            category = "Cellular",
            meaning = "DNA methylation clock reversal & telomere maintenance"
        ),
        LongevityEmojiItem(
            id = "vascular_heart",
            emoji = "🫀",
            title = "Vascular Prodigy",
            category = "Cardio",
            meaning = "Endothelial nitric oxide surge & low arterial stiffness"
        ),
        LongevityEmojiItem(
            id = "mitochondrial_power",
            emoji = "⚡",
            title = "Metabolic Dynamo",
            category = "Metabolism",
            meaning = "High insulin sensitivity & mitochondrial biogenesis"
        ),
        LongevityEmojiItem(
            id = "neuro_brain",
            emoji = "🧠",
            title = "Neuroplastic Master",
            category = "Cognitive",
            meaning = "Prefrontal cortex processing speed & executive reserve"
        ),
        LongevityEmojiItem(
            id = "immune_shield",
            emoji = "🛡️",
            title = "Inflammaging Sentinel",
            category = "Immune",
            meaning = "hs-CRP suppression & low systemic immune inflammation"
        ),
        LongevityEmojiItem(
            id = "muscle_sarcopenia",
            emoji = "🏋️",
            title = "Sarcopenia Armor",
            category = "Physical",
            meaning = "Type-II muscle fiber recruitment & neuromuscular power"
        ),
        LongevityEmojiItem(
            id = "circadian_zen",
            emoji = "🌿",
            title = "Circadian Restorer",
            category = "Recovery",
            meaning = "Deep restorative slow-wave sleep & glymphatic clearance"
        ),
        LongevityEmojiItem(
            id = "time_reversal",
            emoji = "⏳",
            title = "Biological Chrono-Bender",
            category = "Biological",
            meaning = "Biological age tracking below chronological calendar age"
        ),
        LongevityEmojiItem(
            id = "grandmaster_trophy",
            emoji = "🏆",
            title = "Longevity Grandmaster",
            category = "Milestone",
            meaning = "All 7 functional organ reserves scored > 80/100"
        ),
        LongevityEmojiItem(
            id = "diamond_biomarkers",
            emoji = "💎",
            title = "Diamond Biomarkers",
            category = "Clinical",
            meaning = "All blood biomarkers within optimal gerontological ranges"
        ),
        LongevityEmojiItem(
            id = "autophagy_nutrition",
            emoji = "🥗",
            title = "Cellular Autophagy",
            category = "Nutrition",
            meaning = "Polyphenol nutrient sensing & cellular debris clearing"
        ),
        LongevityEmojiItem(
            id = "hormesis_fire",
            emoji = "🔥",
            title = "Hormetic Spark",
            category = "Adaptation",
            meaning = "Heat shock proteins & mitochondrial renewal"
        ),
        LongevityEmojiItem(
            id = "vagus_nerve",
            emoji = "🧘",
            title = "Vagal Harmony",
            category = "Stress",
            meaning = "Parasympathetic tone & heart rate variability mastery"
        ),
        LongevityEmojiItem(
            id = "cryo_adaptation",
            emoji = "🧊",
            title = "Cold Hormesis",
            category = "Adaptation",
            meaning = "Brown adipose tissue activation & metabolic jumpstart"
        ),
        LongevityEmojiItem(
            id = "target_precision",
            emoji = "🎯",
            title = "Protocol Sniper",
            category = "Precision",
            meaning = "Daily actionable protocol completion streak"
        ),
        LongevityEmojiItem(
            id = "centenarian_orbit",
            emoji = "🚀",
            title = "Centenarian Orbit",
            category = "Vision",
            meaning = "90+ projected functional independence lifespan years"
        )
    )

    val achievementBadges: List<LongevityBadge> = listOf(
        LongevityBadge(
            id = "badge_bio_younger",
            emoji = "⏳",
            title = "Age Reversal Pioneer",
            description = "Levine PhenoAge is lower than Chronological Age",
            requirement = "PhenoAge < ChronoAge",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_vascular_shield",
            emoji = "🫀",
            title = "Endothelial Elite",
            description = "Resting pulse < 60 bpm and SBP < 120 mmHg",
            requirement = "Resting HR < 60 bpm",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_metabolic_fire",
            emoji = "⚡",
            title = "Insulin Sensitivity Master",
            description = "HOMA-IR index < 1.4 for optimal glycemic control",
            requirement = "HOMA-IR < 1.4",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_inflammaging_zero",
            emoji = "🛡️",
            title = "Inflammaging Barrier",
            description = "High-sensitivity CRP < 0.5 mg/L",
            requirement = "hs-CRP < 0.5 mg/L",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_cognitive_speed",
            emoji = "🧠",
            title = "Synaptic Velocity",
            description = "Stroop reaction test latency under 550 ms",
            requirement = "Stroop Latency < 550ms",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_muscle_power",
            emoji = "🏋️",
            title = "Sarcopenia Destroyer",
            description = "20+ reps in the 30-Second Chair Stand test",
            requirement = "Chair Stand > 20 reps",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_wearable_synced",
            emoji = "💎",
            title = "Health Connect Synchronized",
            description = "Live wearable telemetry actively feeding the digital twin",
            requirement = "Wearable Synced",
            isUnlocked = true
        ),
        LongevityBadge(
            id = "badge_streak_master",
            emoji = "🔥",
            title = "28-Day Protocol Streak",
            description = "Continuous longevity lifestyle adherence for 4 weeks",
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
            name = "Guest User",
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
