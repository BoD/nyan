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

package org.jraf.nyan.http.graphql

import com.apollographql.apollo.execution.StringCoercing
import com.apollographql.execution.annotation.GraphQLDefault
import com.apollographql.execution.annotation.GraphQLMutation
import com.apollographql.execution.annotation.GraphQLQuery
import com.apollographql.execution.annotation.GraphQLScalar
import kotlinx.coroutines.flow.MutableStateFlow
import org.jraf.klibnanolog.logd

@GraphQLScalar(StringCoercing::class)
typealias ProcessId = String

data class Process(
  val processId: ProcessId,
  val description: String?,
)

sealed interface Result {
  val processId: ProcessId?
}

data class Success(
  override val processId: ProcessId?,
) : Result

data class Error(
  override val processId: ProcessId?,
  val message: String,
) : Result

@GraphQLQuery
class Query(
  private val busyProcesses: MutableStateFlow<Set<org.jraf.nyan.app.Process>>,
) {
  fun busyProcesses(): List<Process> {
    return busyProcesses.value.map { it.toGraphQLProcess() }
  }

  fun isBusy(processId: ProcessId): Boolean {
    return busyProcesses.value.any { it.processId.value == processId }
  }
}

@GraphQLMutation
class Mutation(private val busyIds: MutableStateFlow<Set<org.jraf.nyan.app.Process>>) {
  fun setBusy(
    processId: ProcessId,

    @GraphQLDefault("null")
    description: String?,
  ): Result {
    val process = org.jraf.nyan.app.Process(org.jraf.nyan.app.ProcessId(processId), description)
    logd("setBusy: process=$process")
    busyIds.value = busyIds.value.filter { it.processId.value != processId }.toSet() + process
    return Success(processId = processId)
  }

  fun setIdle(processId: ProcessId): Result {
    logd("setIdle: processId=$processId")
    busyIds.value = busyIds.value.filter { it.processId.value != processId }.toSet()
    return Success(processId = processId)
  }

  fun setAllIdle(): Result {
    busyIds.value = emptySet()
    return Success(processId = null)
  }
}

private fun org.jraf.nyan.app.Process.toGraphQLProcess(): Process {
  return Process(
    processId = processId.value,
    description = description,
  )
}
