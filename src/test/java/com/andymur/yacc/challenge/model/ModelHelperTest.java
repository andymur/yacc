package com.andymur.yacc.challenge.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class ModelHelperTest {

    @Test
    public void testCorrectBidderResponseDeserialization() {
        final String bidderResponseJson = "{\"id\": 1, \"bid\": 500, \"content\": \"price: $price$\"}";
        final BidderResponse bidderResponse = ModelHelper.createBidderResponse(bidderResponseJson);
        Assertions.assertEquals(1, bidderResponse.getId(), "bidder response id has unexpected value");
        Assertions.assertEquals(500, bidderResponse.getBid(), "bidder response bid has unexpected value");
        Assertions.assertEquals("price: $price$", bidderResponse.getContent(), "bidder response has unexpected content");
    }

    @Test
    public void testIncorrectBidderResponseDeserialization() {
        final Set<String> badBidderSerializedResponses = new HashSet<>(
                // test could be parametrized
                Arrays.asList(
                        "{",
                        "{}",
                        "{\"id\": 1, \"bid\": 500}",
                        "{\"id\": 1, \"content\": \"price: $price$\"}",
                        "{\"bid\": 500, \"content\": \"price: $price$\"}"));

        for (final String badResponse: badBidderSerializedResponses) {
            Assertions.assertThrows(RuntimeException.class, () -> {
                ModelHelper.createBidderResponse(badResponse);
            });
        }
    }

    @Test
    public void testBidderRequestSerialization() {
        final Map<String, Integer> attributes = new HashMap<>();
        attributes.put("paramA", 5);
        attributes.put("paramB", 10);
        final String bidderRequest = ModelHelper.createBidderRequest(new BidderRequest(1L, attributes));
        Assertions.assertEquals("{\"id\":1,\"attributes\":{\"paramA\":5,\"paramB\":10}}",
                bidderRequest, "Bidder request serialization failed");
    }
}