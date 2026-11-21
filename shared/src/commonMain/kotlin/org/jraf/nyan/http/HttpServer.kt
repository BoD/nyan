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

package org.jraf.nyan.http

import com.apollographql.apollo.execution.ExecutableSchema
import com.apollographql.execution.ktor.apolloModule
import com.apollographql.execution.ktor.apolloSandboxModule
import io.ktor.http.HttpHeaders
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jraf.klibnanolog.logd
import org.jraf.nyan.VERSION
import org.jraf.nyan.app.Process
import org.jraf.nyan.graphql.NyanExecutableSchemaBuilder
import org.jraf.nyan.http.graphql.Mutation
import org.jraf.nyan.http.graphql.Query

class HttpServer {
  companion object {
    const val PORT = 2026
  }

  val busyProcesses = MutableStateFlow(emptySet<Process>())

  private val executableSchema: ExecutableSchema by lazy {
    NyanExecutableSchemaBuilder()
      .queryRoot { Query(busyProcesses) }
      .mutationRoot { Mutation(busyProcesses) }
      .build()
  }

  suspend fun start(onFailure: (Throwable) -> Unit) {
    logd("Starting http server on port $PORT")
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
      onFailure(throwable)
    }
    try {
      withContext(exceptionHandler) {
        embeddedServer(
          factory = CIO,
          port = PORT,
          parentCoroutineContext = exceptionHandler,
          module = {
            install(DefaultHeaders) {
              header(HttpHeaders.Server, "nyan/${VERSION}")
            }
            apolloModule(executableSchema)
            apolloSandboxModule()
          },
        ).startSuspend(wait = true)
      }
    } catch (e: Exception) {
      if (e is CancellationException) {
        currentCoroutineContext().ensureActive()
      }
      onFailure(e)
    }
  }
}
