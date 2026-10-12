package com.example.conftrack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Everything under "conftrack.api" in application.yml binds here.
 */
@ConfigurationProperties(prefix = "conftrack.api")
public class ApiProperties {

    /** Password for the "qa" login that writes to the API need. Demo only. */
    private String qaPassword;

    public String getQaPassword() {
        return qaPassword;
    }

    public void setQaPassword(String qaPassword) {
        this.qaPassword = qaPassword;
    }
}
