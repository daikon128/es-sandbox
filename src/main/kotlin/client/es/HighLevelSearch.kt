import org.apache.http.HttpHost
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.join.aggregations.JoinAggregationBuilders
import org.elasticsearch.join.aggregations.ParsedChildren
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms
import org.elasticsearch.search.aggregations.metrics.ParsedSum
import org.elasticsearch.search.builder.SearchSourceBuilder

fun highLevelClient() {
    val client = RestHighLevelClient(
        RestClient.builder(HttpHost("localhost", 9200, "http"))
    )

    val searchSourceBuilder = SearchSourceBuilder()
    searchSourceBuilder.size(0)
    val termsAggregationBuilder = AggregationBuilders.terms("stores")
        .field("store_id")
    val productsStocksAggregationBuilder = JoinAggregationBuilders.children("products_stocks", "goods")
    productsStocksAggregationBuilder.subAggregation(
        AggregationBuilders.sum("total_stock")
            .field("stock")
    )
    productsStocksAggregationBuilder.subAggregation(
        AggregationBuilders.filter(
            "limited_products",
            QueryBuilders.termQuery("limited", true)
        )
            .subAggregation(
                AggregationBuilders.sum("total_stock")
                    .field("stock")
            )
    )
    termsAggregationBuilder.subAggregation(productsStocksAggregationBuilder)
    searchSourceBuilder.aggregation(termsAggregationBuilder)
    val searchRequest = SearchRequest()
    searchRequest.indices("store")
    searchRequest.source(searchSourceBuilder)
    println("high level source:" + searchRequest.source())
    val searchResponse = client.search(searchRequest, RequestOptions.DEFAULT)

    of(searchResponse)
    client.close()
}

data class Store(
    val storeId: Int,
    val totalStock: Double,
    val limitedProductsTotalStock: Double
)

fun of(searchResponse: SearchResponse) {
    println(searchResponse.toString())
    (searchResponse.aggregations.get("stores") as ParsedLongTerms).buckets.map {
            storeBucket ->
        val productsStocksBucket = storeBucket.aggregations.get("products_stocks") as ParsedChildren
            val totalStockBucket = productsStocksBucket.aggregations.get("total_stock") as ParsedSum
            val limitedProductsBucket = productsStocksBucket.aggregations.get("limited_products") as ParsedFilter
            val limitedProductsTotalStockBucket = limitedProductsBucket.aggregations.get("total_stock") as ParsedSum
            val store = Store(
                storeId = storeBucket.keyAsString.toInt(),
                totalStock = totalStockBucket.value,
                limitedProductsTotalStock = limitedProductsTotalStockBucket.value
            )
            println(store)
    }
}