package shop

import controllers.oauth.OAuth
import controllers.{Assets, Auctions, Items, Application, Authentication}
import play.api.ApplicationLoader.Context
import play.api.inject.{NewInstanceInjector, SimpleInjector}
import play.api.libs.Crypto
import play.api.libs.crypto.AESCTRCrypter
import play.api.routing.Router
import play.api.{BuiltInComponentsFromContext, ApplicationLoader}
import play.api.cache.{Cached, EhCacheComponents}
import play.api.i18n.I18nComponents
import play.filters.csrf.CSRFComponents
import router.Routes

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.DurationInt

class ShopLoader extends ApplicationLoader {
  def load(context: Context) = (new BuiltInComponentsFromContext(context) with ShopComponents with BootstrapData).application
}

trait ShopComponents extends ShopServiceComponents with EhCacheComponents with I18nComponents with CSRFComponents {

  // --- Controllers

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
  val items = new Items(shop, socialNetwork, oauthCtl, messagesApi)
  val auctions = new Auctions(shop, auctionRooms)
  val assets = new Assets(httpErrorHandler)

  // --- Routers

  lazy val oauthRouter = new oauth.Routes(httpErrorHandler, oauthCtl)
  override lazy val router: Router = new Routes(httpErrorHandler, applicationCtl, authentication, oauthRouter, items, auctions, assets)

  // --- Customize Play components

  val tokenSigner = csrfTokenSigner // WTF
  override lazy val httpFilters = Seq(csrfFilter)

  override lazy val injector = new SimpleInjector(NewInstanceInjector) + router + cookieSigner + csrfTokenSigner + httpConfiguration + tempFileCreator + global + new Crypto(cookieSigner, csrfTokenSigner, new AESCTRCrypter(cryptoConfig))

}

trait BootstrapData extends ShopServiceComponents {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  applicationEvolutions // Force the application of evolutions, if needed
  Await.result(
    for {
      items <- shop.list()
      _ <- if (items.isEmpty) shop.create("Play Framework Essentials", 42) else Future.successful(())
    } yield ()
    , 5.seconds
  )
}