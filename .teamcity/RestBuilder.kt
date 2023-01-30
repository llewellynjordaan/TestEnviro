import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

class RestBuilder(private val request: HttpRequestBase) {

    private var params: List<Pair<String, String>> = emptyList()
    private var headers: List<Pair<String, String>> = emptyList()

    fun withParam(name: String, value: String) {
        this.params = this.params + Pair(name, value)
    }

    fun withHeader(name: String, value: String) {
        this.headers = this.headers + Pair(name, value)
    }

    fun exec(): HttpResponse {
        val httpclient = HttpClients.createDefault()
        if (params.isNotEmpty()) {
            val uriBuilder = URIBuilder(request.uri)
            for ((first, second) in params) {
                uriBuilder.addParameter(first, second)
            }
            request.uri = uriBuilder.build()
        }

        for ((first, second) in headers) {
            request.addHeader(first, second)
        }

        httpclient.execute(request).use { r ->
            val entity = r.entity
            EntityUtils.consume(entity)
            return r
        }
    }
}

fun GET(url: String, init: RestBuilder.() -> Unit) = RestBuilder(HttpPost(url)).apply(init).exec()
