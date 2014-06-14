package models;

import java.util.HashMap;
import java.util.Map;

public interface Users {

    Boolean authenticate(String username, String password);

    public final static Users Users = new Users() {
        @Override
        public Boolean authenticate(String username, String password) {
            return knownUsers.entrySet().stream()
                    .anyMatch(entry -> entry.getKey().equals(username) && entry.getValue().equals(password));
        }
    };

    final static Map<String, String> knownUsers = new HashMap<String, String>() {{
        put("Alice", "aaaa");
        put("Bob", "aaaa");
        put("Carol", "aaaa");
    }};

}
