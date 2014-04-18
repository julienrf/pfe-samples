import play.Application;
import play.GlobalSettings;

import static models.Shop.Shop;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        super.onStart(app);
        if (Shop.list().isEmpty()) {
            Shop.create("Play Framework Essentials", 42.0);
        }
    }
}
