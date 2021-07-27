package com.andymur.yacc.challenge.service.bidding;

import com.andymur.yacc.challenge.model.BidderResponse;

public class Grinder {

    private BidderResponse bestBidderResponse;
    private BidderResponse secondBestBidderResponse;

    public Grinder() {
    }

    // [100, 150, 120, 300, 80] -> 120
    //1st iter
    // 2nd: 100
    // 1st 100
    //2nd iter
    // 2nd: 100
    // 1st: 150
    // 3rd iter
    //
    public void handleResponse(final BidderResponse response) {
        final int currentBid = response.getBid();
        final int currentBestBid = bestBidderResponse != null ? bestBidderResponse.getBid() : 0;

        if (currentBid > currentBestBid) {
            secondBestBidderResponse = bestBidderResponse != null ? bestBidderResponse : response;
            bestBidderResponse = response;
        }

        if (currentBid > secondBestBidderResponse.getBid()) {
            secondBestBidderResponse = response;
        }
    }

    public BidderResponse getBestBidderResponse() {
        return bestBidderResponse;
    }

    public BidderResponse getSecondBestBidderResponse() {
        return secondBestBidderResponse;
    }
}
