import models.Shop
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    super.onStart(app)
    if (Shop.list().isEmpty) {
      Shop.create("Play Framework Essentials", 42)
    }
  }

}
