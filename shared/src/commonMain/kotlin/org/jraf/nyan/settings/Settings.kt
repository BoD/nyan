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

package org.jraf.nyan.settings

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.jraf.klibnanolog.logw
import org.jraf.nyan.util.getHomeDirectory
import org.jraf.nyan.util.getResourcePath

private val busyImageRegex = Regex("busy-\\d{2}\\.png")

class Settings {
  private val settingsDirectory = Path(getHomeDirectory(), ".nyan")
  private val settingsImageDirectory = Path(settingsDirectory, "images")

  private val resourcesImageDirectory by lazy {
    val imageResourcePath = getResourcePath("images")
    // Dev mode only: fallback to etc/images in the project directory if the resource is not found (e.g. when running from the IDE)
    if (imageResourcePath == null) {
      logw("Resource 'images' not found, fallback to etc/images in the project directory")
      Path("../etc/images")
    } else {
      imageResourcePath
    }
  }

  private val imageDirectory by lazy {
    if (SystemFileSystem.exists(settingsImageDirectory)) {
      check(SystemFileSystem.exists(Path(settingsImageDirectory, "idle.png"))) {
        "Settings image directory '$settingsImageDirectory' exists but does not contain 'idle.png'"
      }
      check(SystemFileSystem.list(settingsImageDirectory).any { it.name.matches(busyImageRegex) }) {
        "Settings image directory '$settingsImageDirectory' exists but does not contain any busy images"
      }
      settingsImageDirectory
    } else {
      resourcesImageDirectory
    }
  }

  val idleImage: Path by lazy { Path(imageDirectory, "idle.png") }

  val busyImages: List<Path> by lazy {
    SystemFileSystem.list(imageDirectory)
      .filter { it.name.matches(busyImageRegex) }
      .sortedBy { it.name }
  }

  init {
    ensureInitialized()
  }

  private fun ensureInitialized() {}
}
