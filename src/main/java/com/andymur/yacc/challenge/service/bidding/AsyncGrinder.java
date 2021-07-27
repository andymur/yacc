package com.andymur.yacc.challenge.service.bidding;

import com.andymur.yacc.challenge.model.BidderResponse;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AsyncGrinder {

    private final AtomicInteger counter;
    private final AtomicReference<BidderResponse> bestBidResponseReference;
    private final CompletableFuture<Optional<BidderResponse>> bestBidResponse;

    public AsyncGrinder(int responseNumber) {
        this.counter = new AtomicInteger(responseNumber);
        this.bestBidResponseReference = new AtomicReference<>();
        this.bestBidResponse = new CompletableFuture<>();
    }

    public void handleResponse(final BidderResponse response) {
        BidderResponse currentBestBidderResponse = bestBidResponseReference.get();
        final int currentBid = currentBestBidderResponse != null ? currentBestBidderResponse.getBid() : 0;
        final int newBid = response.getBid();
        counter.decrementAndGet();

        if (newBid > currentBid) {
            boolean done = false;
            while(!done) {
                currentBestBidderResponse = bestBidResponseReference.get();
                done = bestBidResponseReference.compareAndSet(currentBestBidderResponse, response);
            }
        }

        if (counter.get() <= 0) {
            bestBidResponse.complete(Optional.ofNullable(bestBidResponseReference.get()));
        }
    }

    public CompletableFuture<Optional<BidderResponse>> getBestBidderResponseFuture() {
        return bestBidResponse;
    }
}
