package com.andymur.yacc.challenge;

import com.andymur.yacc.challenge.model.BidderRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestHelper {

    public static BidderRequest createBidderRequest(final long id,
                                                     final List<String> attributeKeys,
                                                     final List<Integer> attributeValues) {

        if (attributeKeys.size() != attributeValues.size()) {
            throw new IllegalArgumentException("Number of attribute keys should be the same with number of attribute values");
        }

        final Map<String, Integer> attributes = attributeKeys.stream()
                .collect(Collectors.toMap(key -> key, key -> attributeValues.iterator().next()));

        return new BidderRequest(id, attributes);
    }
}
