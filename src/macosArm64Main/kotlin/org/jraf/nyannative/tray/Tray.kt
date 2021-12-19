package org.jraf.nyannative.tray

import com.autodesk.coroutineworker.CoroutineWorker
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.delay
import org.jraf.nyannative.data.PNG_NYAN_00
import org.jraf.nyannative.data.PNG_NYAN_01
import org.jraf.nyannative.data.PNG_NYAN_02
import org.jraf.nyannative.data.PNG_NYAN_03
import org.jraf.nyannative.data.PNG_NYAN_04
import org.jraf.nyannative.data.PNG_NYAN_05
import org.jraf.nyannative.data.PNG_NYAN_06
import org.jraf.nyannative.data.PNG_NYAN_07
import org.jraf.nyannative.data.PNG_NYAN_08
import org.jraf.nyannative.data.PNG_NYAN_09
import org.jraf.nyannative.data.PNG_NYAN_10
import org.jraf.nyannative.data.PNG_NYAN_11
import platform.AppKit.NSApplication
import platform.AppKit.NSImage
import platform.AppKit.NSStatusBar
import platform.AppKit.NSStatusBarButton
import platform.AppKit.NSStatusItem
import platform.AppKit.NSVariableStatusItemLength
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

actual class Tray {
    actual fun showTray() {
        val app = NSApplication.sharedApplication()
        val item: NSStatusItem = NSStatusBar.systemStatusBar.statusItemWithLength(NSVariableStatusItemLength)
        val button: NSStatusBarButton = item.button()!!
        val images = arrayOf(
            createNSImage(PNG_NYAN_00),
            createNSImage(PNG_NYAN_01),
            createNSImage(PNG_NYAN_02),
            createNSImage(PNG_NYAN_03),
            createNSImage(PNG_NYAN_04),
            createNSImage(PNG_NYAN_05),
            createNSImage(PNG_NYAN_06),
            createNSImage(PNG_NYAN_07),
            createNSImage(PNG_NYAN_08),
            createNSImage(PNG_NYAN_09),
            createNSImage(PNG_NYAN_10),
            createNSImage(PNG_NYAN_11),
        )

        CoroutineWorker.execute {
            while (true) {
                for (image in images) {
                    button.image = image
                    delay(128)
                }
            }
        }

        app.run()
    }

    private fun createNSImage(byteArray: ByteArray) =
        NSImage(data = memScoped { NSData.dataWithBytes(allocArrayOf(byteArray), byteArray.size.convert()) })
}
