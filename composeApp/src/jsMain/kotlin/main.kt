import screen.App
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import di.initKoin
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    initKoin()

    onWasmReady {
        CanvasBasedWindow(canvasElementId = "ComposeTarget") {
            App()
        }
    }

}