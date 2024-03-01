package di

import database.DriverFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

val jvmModule = module {
    single(createdAtStart = true) {
        DriverFactory()
    }
}

fun initKoin() {
    startKoin {
        modules(jvmModule)
        modules(commonModule)
    }
}