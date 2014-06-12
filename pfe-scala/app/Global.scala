import models.Shop
import play.api.mvc.WithFilters
import play.api.{Application, GlobalSettings}
import play.filters.csrf.CSRFFilter

object Global extends WithFilters(CSRFFilter()) {

  override def onStart(app: Application): Unit = {
    super.onStart(app)
    if (Shop.list().isEmpty) {
      Shop.create("Play Framework Essentials", 42)
    }
  }

}
