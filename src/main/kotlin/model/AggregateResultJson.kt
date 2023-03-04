package model

data class SearchResult(
    val took: Int,
    val timed_out: Boolean,
    val _shards: ShardInfo,
    val hits: HitsInfo,
    val aggregations: AggregationsInfo
)

data class ShardInfo(
    val total: Int,
    val successful: Int,
    val skipped: Int,
    val failed: Int
)

data class HitsInfo(
    val total: TotalInfo,
    val max_score: Double?,
    val hits: List<HitInfo>
)

data class TotalInfo(
    val value: Int,
    val relation: String
)

data class HitInfo(
    val foo: Int?
)

data class AggregationsInfo(
    val stores: StoresAggInfo
)

data class StoresAggInfo(
    val doc_count_error_upper_bound: Int,
    val sum_other_doc_count: Int,
    val buckets: List<BucketInfo>
)

data class BucketInfo(
    val key: Int,
    val doc_count: Int,
    val products_stocks: ProductsStocksAggInfo
)

data class ProductsStocksAggInfo(
    val doc_count: Int,
    val limited_products: LimitedProductsAggInfo,
    val total_stock: TotalStockAggInfo
)

data class LimitedProductsAggInfo(
    val doc_count: Int,
    val total_stock: TotalStockAggInfo
)

data class TotalStockAggInfo(
    val value: Double
)