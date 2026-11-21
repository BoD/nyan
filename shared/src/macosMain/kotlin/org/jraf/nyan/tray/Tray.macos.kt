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

@file:OptIn(ExperimentalForeignApi::class)

package org.jraf.nyan.tray

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AppKit.NSImage
import platform.AppKit.NSMenu
import platform.AppKit.NSMenuItem
import platform.AppKit.NSStatusBar
import platform.AppKit.NSStatusItem
import platform.AppKit.NSVariableStatusItemLength
import platform.Foundation.NSMakeSize
import platform.Foundation.NSSelectorFromString

actual fun Tray(): Tray {
  return object : Tray {
    private var statusItem: NSStatusItem? = null

    init {
      val statusBar = NSStatusBar.systemStatusBar
      statusItem = statusBar.statusItemWithLength(NSVariableStatusItemLength)

      val menu = NSMenu()
      val quitItem = NSMenuItem(
        title = "Quit",
        action = NSSelectorFromString("terminate:"),
        keyEquivalent = "q",
      )
      menu.addItem(quitItem)
      statusItem!!.menu = menu
    }

    override fun setImage(absolutePath: String) {
      val nsImage = NSImage(contentsOfFile = absolutePath)
      val targetHeight = 21.0
      nsImage.size = nsImage.size.useContents {
        NSMakeSize(width * targetHeight / height, targetHeight)
      }
      statusItem!!.button!!.image = nsImage
    }
  }
}
