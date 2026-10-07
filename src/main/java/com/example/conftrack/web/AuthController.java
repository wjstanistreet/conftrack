package com.example.conftrack.web;

import com.example.conftrack.config.ApiProperties;
import com.example.conftrack.web.dto.LoginRequest;
import com.example.conftrack.web.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Swaps the QA login for a token that writes to the API must carry. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ApiProperties properties;
    private final TokenStore tokens;

    public AuthController(ApiProperties properties, TokenStore tokens) {
        this.properties = properties;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest login) {
        if (!"qa".equals(login.username())
                || properties.getQaPassword() == null
                || !properties.getQaPassword().equals(login.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password");
        }
        return new LoginResponse(tokens.issue());
    }
}
