package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F;
import play.libs.Scala;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import static controllers.URL.param;

public class OAuth extends Controller {

    public static final String TOKEN_KEY = "oauth-token";

    static final String AUTHORIZATION_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
    static final String TOKEN_ENDPOINT = "https://accounts.google.com/o/oauth2/token";
    static final String CLIENT_ID = "1079303020045-4kie53crgo06su51pi3dnbm90thc2q33.apps.googleusercontent.com";
    static final String CLIENT_SECRET = "9-PoA1ZwynHJlE4Y3VY8fONX";
    static final String SCOPE = "plus.moments.insert https://www.googleapis.com/auth/plus.login";

    static final WSClient ws = WS.client();

    public static String authorizeUrl(Call returnTo) {
        return URL.build(AUTHORIZATION_ENDPOINT, Scala.varargs(
                param("response_type", "code"),
                param("client_id", CLIENT_ID),
                param("redirect_uri", routes.OAuth.callback().absoluteURL(request())),
                param("scope", SCOPE),
                param("state", returnTo.url())
        ));
    }

    public static F.Promise<Result> callback() {
        String code = request().getQueryString("code");
        if (code != null) {
            String state = request().getQueryString("state");
            String returnTo = state != null ? state : routes.Items.list().url();
            return ws.url(TOKEN_ENDPOINT)
                    .setContentType(Http.MimeTypes.FORM)
                    .post(URL.encode(Scala.varargs(
                            param("code", code),
                            param("client_id", CLIENT_ID),
                            param("client_secret", CLIENT_SECRET),
                            param("redirect_uri", routes.OAuth.callback().absoluteURL(request())),
                            param("grant_type", "authorization_code")
                    ))).map(response -> {
                        JsonNode accessTokenJson = response.asJson().get("access_token");
                        if (accessTokenJson == null || !accessTokenJson.isTextual()) {
                            return internalServerError();
                        } else {
                            String accessToken = accessTokenJson.asText();
                            session().put(TOKEN_KEY, accessToken);
                            return redirect(returnTo);
                        }
                    });
        } else {
            return F.Promise.pure(internalServerError());
        }
    }

}
