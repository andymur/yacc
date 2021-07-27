package com.andymur.yacc.challenge.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collection;

@Configuration
public class BiddersConfiguration {
    private final String bidderUrls;

    public BiddersConfiguration(@Value("${bidders}") String bidderUrls) {
        this.bidderUrls = bidderUrls;
    }

    public Collection<String> getBidderUrls() {
        return Arrays.asList(bidderUrls.split(","));
    }
}
