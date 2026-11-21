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

import com.apollographql.apollo.execution.StringCoercing
import com.apollographql.execution.annotation.GraphQLMutation
import com.apollographql.execution.annotation.GraphQLQuery
import com.apollographql.execution.annotation.GraphQLScalar
import kotlinx.coroutines.flow.MutableStateFlow
import org.jraf.klibnanolog.logd

@GraphQLScalar(StringCoercing::class)
typealias ProcessId = String

sealed interface Result {
  val processId: ProcessId
}

data class Success(
  override val processId: ProcessId,
) : Result

data class Error(
  override val processId: ProcessId,
  val message: String,
) : Result

@GraphQLQuery
class Query(private val busyIds: MutableStateFlow<Set<String>>) {
  fun busyProcessIds(): List<ProcessId> {
    return busyIds.value.toList()
  }

  fun isBusy(processId: ProcessId): Boolean {
    return busyIds.value.contains(processId)
  }
}

@GraphQLMutation
class Mutation(private val busyIds: MutableStateFlow<Set<String>>) {
  fun setBusy(processId: ProcessId): Result {
    logd("setBusy: processId=$processId")
    busyIds.value += processId
    return Success(processId = processId)
  }

  fun setIdle(processId: ProcessId): Result {
    logd("setIdle: processId=$processId")
    busyIds.value -= processId
    return Success(processId = processId)
  }
}
