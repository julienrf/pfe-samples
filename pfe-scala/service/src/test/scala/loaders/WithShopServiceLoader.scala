package loaders

import org.specs2.specification.Scope
import play.api.test.Helpers
import play.api._

class WithContext(additionalConfiguration: Map[String, String] = Map.empty, environment: Environment = new Environment(new java.io.File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)) extends Scope {
  val context = {
    val ctx = ApplicationLoader.createContext(environment)
    ctx.copy(initialConfiguration = ctx.initialConfiguration ++ Configuration.from(additionalConfiguration))
  }
}

class WithShopServiceLoader extends WithContext(Helpers.inMemoryDatabase()) {
  val components = new BuiltInComponentsFromContext(context) with ShopServiceComponents
  val shop = components.shop
}
