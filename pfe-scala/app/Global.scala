import play.api.Application
import play.api.mvc.WithFilters
import play.filters.csrf.CSRFFilter
import shop.GuiceInjector

import scala.concurrent.ExecutionContext

object Global extends WithFilters(CSRFFilter()) with GuiceInjector {

  override def onStart(app: Application): Unit = {
    super.onStart(app)
    val service = getControllerInstance(classOf[controllers.Service])
    if (service.shop.list().isEmpty) {
      service.shop.create("Play Framework Essentials", 42)
    }
  }

}
