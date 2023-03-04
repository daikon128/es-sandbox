package client.es

import client.es.util.of
import org.apache.http.HttpHost
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.Request
import org.elasticsearch.client.Response
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder

fun lowLevelSearch() {
    val builder: RestClientBuilder = RestClient.builder(
        HttpHost("localhost", 9200, "http")
    )
    val restClient: RestClient = builder.build()
    val endpoint = "/store/_search"
    val query = """{
  "size": 0,
  "aggs": {
    "stores": {
      "terms": {
        "field": "store_id"
      },
      "aggs": {
        "products_stocks": {
          "children": {
            "type": "goods"
          },
          "aggs": {
            "total_stock": {
              "sum": {
                "field": "stock"
              }
            },
            "limited_products": {
              "filter": {
                "term": {
                  "limited": true
                }
              },
              "aggs": {
                "total_stock": {
                  "sum": {
                    "field": "stock"
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}"""
    val request = Request("POST", endpoint)
    request.setJsonEntity(query)
    val response: Response = restClient.performRequest(request)
    val responseBody: String = EntityUtils.toString(response.entity)
    println(responseBody)
    of(responseBody)
    restClient.close()
}

