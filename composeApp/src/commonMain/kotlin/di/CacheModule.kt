package di

import database.Database
import database.DbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

fun cacheModule() = module {

    single<CoroutineContext>(createdAtStart = true) {
        Dispatchers.Default
    }

    single(createdAtStart = true) {
        CoroutineScope(get())
    }

    single(createdAtStart = true) {
        DbHelper(get())
    }

    single(createdAtStart = true) {
        Database(get(), get())
    }
}