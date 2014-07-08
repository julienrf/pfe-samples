package controllers.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F;
import play.libs.Scala;
import play.libs.ws.WSClient;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import url.URL;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.function.Function;
import java.util.function.Supplier;

import static url.URL.param;

@Singleton
public class OAuth extends Controller {

    public static class Configuration {
        public final String authorizationEndpoint;
        public final String tokenEndpoint;
        public final String clientId;
        public final String clientSecret;
        public final String scope;

        public final String tokenKey;
        public final String defaultReturnUrl;

        public Configuration(String authorizationEndpoint, String tokenEndpoint, String clientId, String clientSecret, String scope, String defaultReturnUrl, String tokenKey) {
            this.authorizationEndpoint = authorizationEndpoint;
            this.tokenEndpoint = tokenEndpoint;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.scope = scope;
            this.tokenKey = tokenKey;
            this.defaultReturnUrl = defaultReturnUrl;
        }

        public Configuration(String authorizationEndpoint, String tokenEndpoint, String clientId, String clientSecret, String scope, String defaultReturnUrl) {
            this(authorizationEndpoint, tokenEndpoint, clientId, clientSecret, scope, defaultReturnUrl, "oauth-token");
        }
    }

    final Configuration configuration;
    final WSClient ws;

    @Inject
    public OAuth(WSClient ws, Configuration configuration) {
        this.ws = ws;
        this.configuration = configuration;
    }

    public String authorizeUrl(Call returnTo) {
        return URL.build(configuration.authorizationEndpoint, Scala.varargs(
                param("response_type", "code"),
                param("client_id", configuration.clientId),
                param("redirect_uri", routes.OAuth.callback().absoluteURL(request())),
                param("scope", configuration.scope),
                param("state", returnTo.url())
        ));
    }

    public F.Promise<Result> callback() {
        String code = request().getQueryString("code");
        if (code != null) {
            String state = request().getQueryString("state");
            String returnTo = state != null ? state : configuration.defaultReturnUrl;
            return ws.url(configuration.tokenEndpoint)
                    .setContentType(Http.MimeTypes.FORM)
                    .post(URL.encode(Scala.varargs(
                            param("code", code),
                            param("client_id", configuration.clientId),
                            param("client_secret", configuration.clientSecret),
                            param("redirect_uri", routes.OAuth.callback().absoluteURL(request())),
                            param("grant_type", "authorization_code")
                    ))).map(response -> {
                        JsonNode accessTokenJson = response.asJson().get("access_token");
                        if (accessTokenJson == null || !accessTokenJson.isTextual()) {
                            return internalServerError();
                        } else {
                            String accessToken = accessTokenJson.asText();
                            session().put(configuration.tokenKey, accessToken);
                            return redirect(returnTo);
                        }
                    });
        } else {
            return F.Promise.pure(internalServerError());
        }
    }

    // Must be called within the scope of an Action
    public <A> A authenticated(Function<String, A> f, Supplier<A> g) {
        String token = session(configuration.tokenKey);
        if (token != null) return f.apply(token);
        else return g.get();
    }

}
