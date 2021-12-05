/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2021 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.nyan

import kotlin.system.exitProcess
import java.io.File
import org.jraf.nyan.timelog.TimeLog
import org.jraf.nyan.tray.Tray

private fun onProgressOngoing(progressOngoing: Boolean) {
    if (progressOngoing) {
        TimeLog.startCountingTime()
        Tray.showIcon()
    } else {
        TimeLog.stopCountingTime()
        Tray.hideIcon()
    }
}

private fun runCommand(workingDir: File, command: List<String>): Int {
    val process = ProcessBuilder(command)
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()

    return process.waitFor()
}


fun main(args: Array<String>) {
    onProgressOngoing(true)
    val exitCode = try {
        runCommand(File("."), args.toList())
    } catch (t: Throwable) {
        System.err.println(t.message)
        -1
    }
    onProgressOngoing(false)
    exitProcess(exitCode)
}