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

package org.jraf.nyan.tray

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities
import org.jraf.nyan.VERSION
import org.jraf.nyan.images.Images
import org.jraf.nyan.timelog.TimeLog
import org.jraf.nyan.util.asFormattedDate
import org.jraf.nyan.util.asFormattedDuration

object Tray {
    private const val MENU_ITEM_OVERALL = "Since %s: %s"
    private const val MENU_ITEM_YEAR = "This year: %s"
    private const val MENU_ITEM_MONTH = "This month: %s"
    private const val MENU_ITEM_WEEK = "This week: %s"
    private const val MENU_ITEM_DAY = "Today: %s"
    private const val MENU_ITEM_INFO = "(1d = 1 work day = 8 hours)"
    private const val MENU_ITEM_ABOUT = "BoD Nyan $VERSION - https://JRAF.org"

    private const val ANIMATION_DELAY_MS = 128L
    private var animationJob: Job? = null

    private val showing = AtomicBoolean(false)

    private val overallMenuItem by lazy {
        MenuItem(getFormattedTimeLogOverall()).apply {
            isEnabled = false
        }
    }

    private val yearMenuItem by lazy {
        MenuItem(getFormattedTimeLogYear()).apply {
            isEnabled = false
        }
    }

    private val monthMenuItem by lazy {
        MenuItem(getFormattedTimeLogMonth()).apply {
            isEnabled = false
        }
    }
    private val weekMenuItem by lazy {
        MenuItem(getFormattedTimeLogWeek()).apply {
            isEnabled = false
        }
    }
    private val dayMenuItem by lazy {
        MenuItem(getFormattedTimeLogDay()).apply {
            isEnabled = false
        }
    }
    private val trayIcon: TrayIcon by lazy {
        TrayIcon(
            Images.nyan[0],
            MENU_ITEM_ABOUT,
            PopupMenu().apply {
                add(overallMenuItem)
                add(yearMenuItem)
                add(monthMenuItem)
                add(weekMenuItem)
                add(dayMenuItem)
                add(MenuItem("-"))
                add(
                    MenuItem(MENU_ITEM_INFO).apply {
                        isEnabled = false
                    }
                )
                add(
                    MenuItem(MENU_ITEM_ABOUT).apply {
                        isEnabled = false
                    }
                )
            }
        )
    }

    fun showIcon() {
        if (showing.get()) return
        if (!SystemTray.isSupported()) return
        showing.set(true)
        SwingUtilities.invokeLater {
            updateMenuItems()
            SystemTray.getSystemTray().add(trayIcon)
            animationJob = GlobalScope.launch { startTrayIconAnimation() }
        }
    }

    fun hideIcon() {
        if (!showing.get()) return
        if (!SystemTray.isSupported()) return
        showing.set(false)
        animationJob?.cancel()
        SwingUtilities.invokeLater {
            SystemTray.getSystemTray().remove(trayIcon)
        }
    }

    private fun updateMenuItems() {
        overallMenuItem.label = getFormattedTimeLogOverall()
        yearMenuItem.label = getFormattedTimeLogYear()
        monthMenuItem.label = getFormattedTimeLogMonth()
        weekMenuItem.label = getFormattedTimeLogWeek()
        dayMenuItem.label = getFormattedTimeLogDay()
    }

    private suspend fun startTrayIconAnimation() {
        while (true) {
            for (i in Images.nyan) {
                trayIcon.image = i
                delay(ANIMATION_DELAY_MS)
            }
        }
    }

    private fun getFormattedTimeLogOverall() =
        MENU_ITEM_OVERALL.format(TimeLog.firstUse.asFormattedDate(), TimeLog.countedTimeOverall.asFormattedDuration())

    private fun getFormattedTimeLogYear() = MENU_ITEM_YEAR.format(TimeLog.countedTimeYear.asFormattedDuration())
    private fun getFormattedTimeLogMonth() = MENU_ITEM_MONTH.format(TimeLog.countedTimeMonth.asFormattedDuration())
    private fun getFormattedTimeLogWeek() = MENU_ITEM_WEEK.format(TimeLog.countedTimeWeek.asFormattedDuration())
    private fun getFormattedTimeLogDay() = MENU_ITEM_DAY.format(TimeLog.countedTimeDay.asFormattedDuration())
}
