package models;

import java.util.HashMap;
import java.util.Map;

public interface Users {
    Boolean authenticate(String username, String password);

    public final static Users Users = new Users() {
        @Override
        public Boolean authenticate(String username, String password) {
            return null;
        }
    };

    final static Map<String, String> knownUsers = new HashMap<String, String>() {{
        put("Alice", "aaaa");
        put("Bob", "aaaa");
        put("Carol", "aaaa");
    }};

}
