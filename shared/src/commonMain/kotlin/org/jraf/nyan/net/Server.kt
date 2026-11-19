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

package org.jraf.nyan.net

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.utils.io.readByte
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jraf.klibnanolog.logd

private const val PORT = 2026

class Server {
  val atLeastOneConnected: StateFlow<Boolean> field = MutableStateFlow(false)

  private var connectedSocketCount = 0

  suspend fun start() {
    logd("Starting server on port $PORT")
    val serverSocket = try {
      aSocket(SelectorManager(Dispatchers.IO)).tcp().bind(port = PORT)
    } catch (e: Exception) {
      logd(e, "Could not start server on port $PORT")
      return
    }
    while (true) {
      val clientSocket = serverSocket.accept()
      connectedSocketCount++
      logd("Accepted connection from ${clientSocket.remoteAddress}, connectedSocketCount=$connectedSocketCount")
      atLeastOneConnected.value = true
      clientSocket.socketContext.invokeOnCompletion {
        connectedSocketCount--
        logd("Connection from ${clientSocket.remoteAddress} closed, connectedSocketCount=$connectedSocketCount")
        atLeastOneConnected.value = connectedSocketCount > 0
      }
      clientSocket.launch {
        try {
          while (true) {
            clientSocket.openReadChannel().readByte()
          }
        } catch (e: Exception) {
          if (e is CancellationException) {
            ensureActive()
          } else {
            clientSocket.close()
          }
        }
      }
    }
  }
}
