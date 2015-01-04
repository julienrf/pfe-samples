package loaders

import org.openqa.selenium.WebDriver
import org.specs2.execute.{Result, AsResult}
import org.specs2.mutable.Around
import org.specs2.specification.Scope
import play.api.ApplicationLoader.Context
import play.api._
import play.api.test._

object `package` {

  val fakeContext = {
    val ctx = ApplicationLoader.createContext(new Environment(new java.io.File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test))
    ctx.copy(initialConfiguration = ctx.initialConfiguration ++ Configuration.from(Helpers.inMemoryDatabase()))
  }

  val fakeShopLoader = new ApplicationLoader {
    def load(context: Context) = (new BuiltInComponentsFromContext(context) with ShopComponents).application
  }

}

class WithShopApplication extends WithApplicationLoader(fakeShopLoader, fakeContext)

abstract class WithBrowser[WEBDRIVER <: WebDriver](
    val webDriver: WebDriver = WebDriverFactory(Helpers.HTMLUNIT),
    val app: Application = FakeApplication(),
    val port: Int = Helpers.testServerPort) extends Around with Scope {

  implicit def implicitApp: play.api.Application = app
  implicit def implicitPort: Port = port

  lazy val browser: TestBrowser = TestBrowser(webDriver, Some("http://localhost:" + port))

  override def around[T: AsResult](t: => T): Result = {
    try {
      Helpers.running(TestServer(port, app))(AsResult.effectively(t))
    } finally {
      browser.quit()
    }
  }
}

class WithShopBrowser extends WithBrowser(app = fakeShopLoader.load(fakeContext))