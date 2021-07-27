package com.andymur.yacc.challenge.model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ModelHelper {

    public static BidderResponse createBidderResponse(String bidderResponseAsAString) {
        try {
            return new ObjectMapper().readValue(bidderResponseAsAString, BidderResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createBidderRequest(BidderRequest bidderRequest) {
        try {
            return new ObjectMapper().writeValueAsString(bidderRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
