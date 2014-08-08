import play.api.Application
import play.api.mvc.WithFilters
import play.filters.csrf.CSRFFilter
import shop.ShopInjector

object Global extends WithFilters(CSRFFilter()) with ShopInjector {

  override def onStart(app: Application): Unit = {
    super.onStart(app)
    val service = getControllerInstance(classOf[controllers.Service])
    if (service.shop.list().isEmpty) {
      service.shop.create("Play Framework Essentials", 42)
    }
  }

}
