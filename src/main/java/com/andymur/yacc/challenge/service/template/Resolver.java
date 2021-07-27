package com.andymur.yacc.challenge.service.template;

import com.andymur.yacc.challenge.model.BidderResponse;

public interface Resolver {
    String resolve(String content, BidderResponse bidderResponse);
}
