package org.jraf.nyannative.system

actual fun exitProcess(status: Int): Nothing = kotlin.system.exitProcess(status)
