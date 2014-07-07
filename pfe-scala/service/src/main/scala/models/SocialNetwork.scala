package models

import play.api.libs.ws.{WSClient, WSResponse}
import scala.concurrent.Future

class SocialNetwork(ws: WSClient) {

  val sharingEndpoint = "http://www.mocky.io/v2/539c5a907650aa6202515c00"

  def share(content: String, token: String): Future[WSResponse] =
    ws.url(sharingEndpoint)
      .withQueryString("access_token" -> token)
      .post(Map("content" -> Seq(content)))

}
