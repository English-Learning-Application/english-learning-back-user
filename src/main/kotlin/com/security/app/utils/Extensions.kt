package com.security.app.utils

import java.util.*

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}