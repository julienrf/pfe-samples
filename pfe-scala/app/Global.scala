import play.api.Application
import play.api.mvc.WithFilters
import play.filters.csrf.CSRFFilter

object Global extends WithFilters(CSRFFilter()) {

  override def onStart(app: Application): Unit = {
    super.onStart(app)
    val service = app.injector.instanceOf[controllers.Service]
    if (service.shop.list().isEmpty) {
      service.shop.create("Play Framework Essentials", 42)
    }
  }

}
