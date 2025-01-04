package com.security.app.model

enum class Language(val value: String) {
    VIETNAMESE("vi"),
    ENGLISH("en");

    companion object {
        fun fromString(value: String): Language {
            return when(value) {
                "vi" -> VIETNAMESE
                "en" -> ENGLISH
                else -> throw IllegalArgumentException("Language not found")
            }
        }
    }
}

enum class LearningType(val value: String) {
    VOCABULARY("vocabulary"),
    GRAMMAR("grammar"),
    LISTENING("listening"),
    SPEAKING("speaking"),
    READING("reading"),
    WRITING("writing");

    companion object {
        fun fromString(value: String): LearningType {
            return when(value) {
                "vocabulary" -> VOCABULARY
                "grammar" -> GRAMMAR
                "listening" -> LISTENING
                "speaking" -> SPEAKING
                "reading" -> READING
                "writing" -> WRITING
                else -> throw IllegalArgumentException("Learning type not found")
            }
        }
    }
}

enum class RegistrationStatus {
    NOT_CONFIRMED,
    CONFIRMED
}