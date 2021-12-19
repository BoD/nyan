#!/usr/bin/env kotlin

import java.io.File

for (i in 0..11) {
    val imageFile = File("etc/nyan-originals/nyan%05d.png".format(i))
    val processBuilder =
        ProcessBuilder(
            "scripts/binary2kotlin.main.kts",
            "org.jraf.nyannative.data",
            "PNG_NYAN_%02d".format(i),
            imageFile.absolutePath
        )
    processBuilder.redirectOutput(File("src/commonMain/kotlin/org/jraf/nyannative/data/PngNyan%02d.kt".format(i)))
    processBuilder.start().waitFor()
}
