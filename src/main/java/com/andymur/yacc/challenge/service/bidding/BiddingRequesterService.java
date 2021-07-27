package com.andymur.yacc.challenge.service.bidding;

import com.andymur.yacc.challenge.model.BidderRequest;
import com.andymur.yacc.challenge.model.BidderResponse;

import java.util.Collection;
import java.util.Optional;

public interface BiddingRequesterService {
    Optional<BidderResponse> getBestBidderResponse(final Collection<String> bidderUrls, final BidderRequest bidderRequest);
}
