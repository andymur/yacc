package com.andymur.yacc.challenge.service.template;

import com.andymur.yacc.challenge.model.BidderResponse;


public class BidPriceResolver implements Resolver {

    private static final String TEMPLATE = "$price$";

    @Override
    public String resolve(String content, BidderResponse bidderResponse) {
        return content.replace(TEMPLATE, String.valueOf(bidderResponse.getBid()));
    }
}
