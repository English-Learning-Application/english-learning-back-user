package com.security.app.model

enum class Language(val value: String) {
    VIETNAMESE("VIETNAMESE"),
    ENGLISH("ENGLISH"),
    FRENCH("FRENCH");

    companion object {
        fun fromString(value: String): Language {
            return when (value) {
                "VIETNAMESE" -> VIETNAMESE
                "ENGLISH" -> ENGLISH
                "FRENCH" -> FRENCH
                else -> throw IllegalArgumentException("Language not found")
            }
        }
    }
}

enum class LearningType(val value: String) {
    VOCABULARY("VOCABULARY"),
    GRAMMAR("GRAMMAR"),
    LISTENING("LISTENING"),
    SPEAKING("SPEAKING"),
    READING("READING"),
    WRITING("WRITING");

    companion object {
        fun fromString(value: String): LearningType {
            return when (value) {
                "VOCABULARY" -> VOCABULARY
                "GRAMMAR" -> GRAMMAR
                "LISTENING" -> LISTENING
                "SPEAKING" -> SPEAKING
                "READING" -> READING
                "WRITING" -> WRITING
                else -> throw IllegalArgumentException("Learning type not found")
            }
        }
    }
}

enum class RegistrationStatus {
    NOT_CONFIRMED,
    CONFIRMED
}

enum class UpdateUserProfileType(val serverValue: String) {
    SUBSCRIPTION("SUBSCRIPTION"),
    ACHIEVEMENT("ACHIEVEMENT");

    companion object {
        fun fromString(value: String): UpdateUserProfileType {
            return when (value) {
                "SUBSCRIPTION" -> SUBSCRIPTION
                "ACHIEVEMENT" -> ACHIEVEMENT
                else -> throw IllegalArgumentException("Update user profile type not found")
            }
        }
    }
}

enum class DurationLength(val serverValue: String) {
    DAY("DAY"),
    WEEK("WEEK"),
    MONTH("MONTH"),
    YEAR("YEAR");

    companion object {
        fun fromServerValue(serverValue: String): DurationLength {
            return when (serverValue) {
                "DAY" -> DAY
                "WEEK" -> WEEK
                "MONTH" -> MONTH
                "YEAR" -> YEAR
                else -> throw IllegalArgumentException("Invalid value $serverValue")
            }
        }
    }
}
