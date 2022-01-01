
import com.autodesk.coroutineworker.CoroutineWorker
import org.jraf.nyannative.exec.executeCommand
import org.jraf.nyannative.system.exitProcess
import org.jraf.nyannative.tray.Tray

fun main(av: Array<String>) {
    CoroutineWorker.execute {
        val resultCode = executeCommand(av.asList())
        exitProcess(resultCode)
    }
    Tray().showTray()
}
