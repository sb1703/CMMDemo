package di

import HomeRepository
import HomeViewModel
import org.koin.dsl.module

val commonModule = networkModule() + module {

    single(createdAtStart = true) {
        HomeRepository(get())
    }

    single(createdAtStart = true) {
        HomeViewModel(get())
    }

}