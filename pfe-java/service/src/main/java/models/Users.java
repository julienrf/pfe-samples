package models;

import java.util.HashMap;
import java.util.Map;

public class Users {

    public Boolean authenticate(String username, String password) {
        return knownUsers.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(username) && entry.getValue().equals(password));
    }

    final Map<String, String> knownUsers = new HashMap<String, String>() {{
        put("Alice", "aaaa");
        put("Bob", "aaaa");
        put("Carol", "aaaa");
    }};

}
