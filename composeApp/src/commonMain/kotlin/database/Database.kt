package database

import app.cash.sqldelight.async.coroutines.awaitAsList
import data.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class Database(
    private val dbHelper: DbHelper,
    private val scope: CoroutineScope
) {

    fun clearDatabase() {
        scope.launch {
            dbHelper.withDatabase { database ->
                database.appDatabaseQueries.removeAllProducts()
            }
        }
    }

    suspend fun getAllProducts(): List<Product> {
        var items: List<Product>
        val result = scope.async {
            dbHelper.withDatabase { database ->
                items = database.appDatabaseQueries.selectAllProducts(::mapProductSelecting).awaitAsList()
                items
            }
        }

        return result.await()
    }

    private fun mapProductSelecting(
        id: Long,
        title: String,
        image: String,
        price: String,
        category: String?,
        description: String?
    ): Product {

        return Product(
            id = id.toInt(),
            price = price.toDouble(),
            category = category,
            description = description,
            title = title,
            image = image
        )
    }

    suspend fun createProducts(items: List<Product>) {
        val result = scope.async {
            dbHelper.withDatabase { database ->
                items.forEach {
                    insertProducts(it)
                }
            }
        }
    }

    private suspend fun insertProducts(item: Product) {
        scope.async {
            dbHelper.withDatabase { database ->
                database.appDatabaseQueries.insertProduct(
                    id = item.id?.toLong(),
                    title = item.title.toString(),
                    image = item.image.toString(),
                    price = (item.price ?: 0.0).toString(),
                    category = item.category.toString(),
                    description = item.description.toString()
                )
            }
        }
    }

}