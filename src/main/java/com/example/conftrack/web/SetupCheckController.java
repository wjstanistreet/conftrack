package com.example.conftrack.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** The only thing this branch does. If you can see it, your setup is good. */
@RestController
public class SetupCheckController {

    @GetMapping(value = "/", produces = "text/html")
    public String ok() {
        return """
                <!doctype html>
                <html lang="en">
                <head><meta charset="utf-8"><title>ConfTrack setup check</title></head>
                <body style="font:16px/1.5 system-ui,sans-serif;max-width:34rem;margin:12vh auto;padding:0 1rem">
                  <h1 style="font-size:3rem;margin:0">OK</h1>
                  <p>Java, Gradle and Spring Boot all work on this machine, and your dependency
                     cache is warm for the workshop.</p>
                  <p>See you on the day!</p>
                </body>
                </html>
                """;
    }
}
