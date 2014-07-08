package models;

import url.URL;
import play.libs.F;
import play.libs.Scala;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http;

public class SocialNetwork {

    final WSClient ws;

    final String SHARING_ENDPOINT = "http://www.mocky.io/v2/539c5a907650aa6202515c00";

    public SocialNetwork(WSClient ws) {
        this.ws = ws;
    }

    public F.Promise<WSResponse> share(String content, String token) {
        return ws.url(SHARING_ENDPOINT)
                .setQueryParameter("access_token", token)
                .setContentType(Http.MimeTypes.FORM)
                .post(URL.encode(Scala.varargs(URL.param("content", content))));
    }

}
