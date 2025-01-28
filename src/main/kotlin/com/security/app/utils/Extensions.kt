package com.security.app.utils

import java.time.LocalDateTime
import java.util.*

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}

fun LocalDateTime.isAfterNow(): Boolean {
    return this.isAfter(LocalDateTime.now())
}