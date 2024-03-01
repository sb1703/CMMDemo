import androidx.compose.ui.window.ComposeUIViewController
import database.DriverFactory
import di.commonModule
import org.koin.core.context.startKoin
import org.koin.dsl.module
import screen.App

fun MainViewController() = ComposeUIViewController {
    App()
}

val iosModule = module {
    single(createdAtStart = true) {
        DriverFactory()
    }
}

fun initKoin() {
    startKoin {
        modules(iosModule)
        modules(commonModule)
    }
}
