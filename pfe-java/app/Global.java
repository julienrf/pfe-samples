import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;

import static models.Shop.Shop;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        super.onStart(app);
        if (Shop.list().isEmpty()) {
            Shop.create("Play Framework Essentials", 42.0);
        }
    }

    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{CSRFFilter.class};
    }
}
