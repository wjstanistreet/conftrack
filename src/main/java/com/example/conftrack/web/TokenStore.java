package com.example.conftrack.web;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** The tokens handed out since the application started. A restart forgets them all. */
@Component
public class TokenStore {

    private final Set<String> issued = ConcurrentHashMap.newKeySet();

    public String issue() {
        String token = UUID.randomUUID().toString();
        issued.add(token);
        return token;
    }

    public boolean isValid(String token) {
        return token != null && issued.contains(token);
    }
}
