package url

// Convenient URL builder
object URL {

  case class Param(name: String, value: String)

  def param(name: String, value: String) = Param(name, value)

  def build(endpoint: String, params: Param*) = {
    endpoint + "?" + encode(params: _*)
  }

  def encode(params: Param*) = {
    import java.net.URLEncoder.{encode => enc}
    val queryString = for (Param(n, v) <- params) yield s"""${enc(n, "utf-8")}=${enc(v, "utf-8")}"""
    queryString.toSeq.mkString("&")
  }

}
