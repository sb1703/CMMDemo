import androidx.compose.ui.window.ComposeUIViewController
import di.commonModule
import org.koin.core.context.startKoin
import screen.App

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initKoin() {
    startKoin {
        modules(commonModule)
    }
}
