#!/usr/bin/env kotlin

import java.io.File

val packageName = args[0]
val valName = args[1]
val fileName = args[2]

val file = File(fileName)
val bytes = file.readBytes().toList()
val chunks = bytes.chunked(32)
println(
    """
    package $packageName
    
    val $valName = byteArrayOf(
        ${chunks.joinToString("\n        ") {
            it.joinToString(", ", postfix = ",")
        }}
    )
    """.trimIndent()
)
