package com.example.conftrack.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Writes to the API need "Authorization: Bearer <token>", with a token from
 * POST /api/auth/login. Reads stay open, so the dashboard page needs nothing.
 */
@Component
public class BearerTokenFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";

    private final TokenStore tokens;

    public BearerTokenFilter(TokenStore tokens) {
        this.tokens = tokens;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !HttpMethod.POST.matches(request.getMethod())
                || !path.startsWith("/api/")
                || path.equals("/api/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = header != null && header.startsWith(PREFIX) ? header.substring(PREFIX.length()).trim() : null;
        if (tokens.isValid(token)) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"Writes need a token. Log in with POST /api/auth/login"
                + " and send it as Authorization: Bearer <token>.\"}");
    }
}
