package org.jraf.nyannative.exec

import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen
import platform.posix.posix_errno

// Inspired by https://stackoverflow.com/questions/57123836/
actual fun executeCommand(command: String): Int {
    val commandToExecute = "$command 2>&1"
    // Apparently the error case actually never happens since this actually executes sh. Keeping it anyway just in case
    val fp = popen(commandToExecute, "r") ?: error("Could not execute command: ${posix_errno()}")
    val buffer = ByteArray(4096)
    while (true) {
        val input: CPointer<ByteVarOf<Byte>> = fgets(buffer.refTo(0), buffer.size, fp) ?: break
        print(input.toKString())
    }
    return pclose(fp)
}
