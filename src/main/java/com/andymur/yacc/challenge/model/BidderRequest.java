package com.andymur.yacc.challenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class BidderRequest {
    private final long id;
    private final Map<String, Integer> requestAttributes;

    public BidderRequest(final long id, final Map<String, Integer> requestAttributes) {
        this.id = id;
        this.requestAttributes = requestAttributes;
    }

    public long getId() {
        return id;
    }

    @JsonProperty("attributes")
    public Map<String, Integer> getRequestAttributes() {
        return requestAttributes;
    }

    @Override
    public String toString() {
        return "BidderRequest{" +
                "id=" + id +
                ", requestAttributes=" + requestAttributes +
                '}';
    }
}
