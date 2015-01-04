package loaders

import controllers._
import controllers.oauth.OAuth
import play.api.ApplicationLoader.Context
import play.api.cache.{Cached, EhCacheComponents}
import play.api.http.DefaultHttpRequestHandler
import play.api.i18n.I18nComponents
import play.api.{ApplicationLoader, BuiltInComponentsFromContext}
import play.filters.csrf.CSRFFilter

class ShopLoader extends ApplicationLoader {
  def load(context: Context) = (new BuiltInComponentsFromContext(context) with ShopComponents with BootstrapData).application
}

trait ShopComponents extends ShopServiceComponents with EhCacheComponents with I18nComponents {

  // --- Customize Play components

  override lazy val httpRequestHandler = new DefaultHttpRequestHandler(routes, httpErrorHandler, httpConfiguration, new CSRFFilter())

  // --- Controllers

  val dbAction = new DBAction(actorSystem)
  val applicationCtl = new Application(new Cached(defaultCacheApi), messagesApi)
  val authentication = new Authentication(users, messagesApi)
  val oauthConfig = OAuth.Configuration(
    authorizationEndpoint = "https://accounts.google.com/o/oauth2/auth",
    tokenEndpoint = "https://accounts.google.com/o/oauth2/token",
    clientId = "1079303020045-4kie53crgo06su51pi3dnbm90thc2q33.apps.googleusercontent.com",
    clientSecret = "9-PoA1ZwynHJlE4Y3VY8fONX",
    scope = "https://www.googleapis.com/auth/plus.login",
    defaultReturnUrl = controllers.routes.Items.list().url
  )
  val oauthCtl = new OAuth(wsClient, oauthConfig)(play.api.libs.concurrent.Execution.defaultContext)
  val items = new Items(shop, socialNetwork, dbAction, oauthCtl)
  val auctions = new Auctions(shop, auctionRooms, dbAction)
  val assets = new Assets(httpErrorHandler)

  // --- Routers

  lazy val oauthRoutes = new _root_.oauth.Routes(httpErrorHandler, oauthCtl)
  override lazy val routes = new _root_.shop.Routes(httpErrorHandler, applicationCtl, authentication, oauthRoutes, items, auctions, assets)
}

trait BootstrapData extends ShopServiceComponents {
  applicationEvolutions // Force the application of evolutions, if needed
  if (shop.list().isEmpty) {
    shop.create("Play Framework Essentials", 42)
  }
}