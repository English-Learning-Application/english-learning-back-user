package com.security.app.model

enum class Language(val value: String) {
    VIETNAMESE("VI"),
    ENGLISH("EN");

    companion object {
        fun fromString(value: String): Language {
            return when(value) {
                "VI" -> VIETNAMESE
                "EN" -> ENGLISH
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
            return when(value) {
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