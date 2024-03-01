package org.example.project

import android.app.Application
import database.DriverFactory
import di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val module = module {
            single(createdAtStart = true) {
                DriverFactory(applicationContext)
            }
        }

        startKoin {
            androidContext(this@MainApplication)
            androidLogger()

            modules(module)
            modules(commonModule)
        }
    }
}