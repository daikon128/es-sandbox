package client.es.util

import com.google.gson.Gson
import model.SearchResult

fun of(json: String) {
    val gson = Gson()
    val result = gson.fromJson(json, SearchResult::class.java)
    result.aggregations.stores.buckets.forEach { bucket ->
        println(
            "Store ${bucket.key}: Total stock=${bucket.products_stocks.total_stock.value}, " +
                    "Limited total stock=${bucket.products_stocks.limited_products.total_stock.value}"
        )
    }
}
