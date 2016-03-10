package shop

import play.api.ApplicationLoader.Context
import play.api.test.{Helpers, WithApplication, WithBrowser}
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Configuration, Environment}

object `package` {

  val fakeContext = {
    val ctx = ApplicationLoader.createContext(Environment.simple())
    ctx.copy(initialConfiguration = ctx.initialConfiguration ++ Configuration.from(Helpers.inMemoryDatabase()))
  }

  val fakeShopLoader = new ApplicationLoader {
    def load(context: Context) =
      (new BuiltInComponentsFromContext(context) with ShopComponents).application
  }

}

class WithShopApplication extends WithApplication(app = fakeShopLoader.load(fakeContext))

class WithShopBrowser extends WithBrowser(app = fakeShopLoader.load(fakeContext))
