package com.security.app

import com.security.app.services.UserService
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties


fun listClassFeatures(clazz: KClass<*>) {
    println("Properties:")
    clazz.memberProperties.forEach { println(it.name) }

    println("\nFunctions:")
    clazz.memberFunctions.forEach { println(it.name) }
}

fun main() {
    listClassFeatures(UserService::class) // Replace MyClass with your class name
}
