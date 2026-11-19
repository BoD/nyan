/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2026-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.nyan.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jraf.klibnanolog.logd
import org.jraf.nyan.net.Server
import org.jraf.nyan.settings.Settings
import org.jraf.nyan.tray.Tray
import kotlin.time.Duration.Companion.milliseconds

class App {
  private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

  private val server = Server()
  private val settings = Settings()
  private val tray = Tray()

  private var busyAnimationJob: Job? = null

  private fun startBusyAnimation() {
    if (busyAnimationJob != null) return
    busyAnimationJob = coroutineScope.launch {
      while (true) {
        for (image in settings.busyImages) {
          withContext(Dispatchers.Main) {
            tray.setImage(image.toString())
          }
          delay(120.milliseconds)
        }
      }
    }
  }

  private fun stopBusyAnimation() {
    busyAnimationJob?.cancel()
    busyAnimationJob = null
  }

  fun start() {
    logd("Hello, World!")

    val application = PlatformApplication()
    tray.show()

    coroutineScope.launch {
      launch(Dispatchers.IO) {
        server.start()
      }

      server.atLeastOneConnected.collect { atLeastOneConnected ->
        logd("atLeastOneConnected=$atLeastOneConnected")
        if (atLeastOneConnected) {
          startBusyAnimation()
        } else {
          stopBusyAnimation()
          withContext(Dispatchers.Main) {
            tray.setImage(settings.idleImage.toString())
          }
        }
      }
    }

    try {
      // Blocking
      application.run()
    } finally {
      coroutineScope.cancel()
    }
  }
}
