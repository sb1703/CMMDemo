package di

import HomeRepository
import HomeViewModel
import database.datasource.ProductsLocalDataSource
import database.datasource.ProductsRemoteDataSource
import org.koin.dsl.module

val commonModule = cacheModule() + networkModule() + module {

    single(createdAtStart = true) {
        ProductsLocalDataSource(get())
    }

    single(createdAtStart = true) {
        ProductsRemoteDataSource(get())
    }

    single(createdAtStart = true) {
        HomeRepository(get(), get())
    }

    single(createdAtStart = true) {
        HomeViewModel(get())
    }

}