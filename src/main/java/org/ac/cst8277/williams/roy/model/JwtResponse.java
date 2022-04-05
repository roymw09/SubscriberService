package org.ac.cst8277.williams.roy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtResponse {

    @JsonProperty("token")
    private String jwttoken;

    public JwtResponse() {}

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}
