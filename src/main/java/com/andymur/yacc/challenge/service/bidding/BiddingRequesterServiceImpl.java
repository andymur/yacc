package com.andymur.yacc.challenge.service.bidding;

import com.andymur.yacc.challenge.model.BidderRequest;
import com.andymur.yacc.challenge.model.BidderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value = "biddingRequesterService.type", havingValue = "sync", matchIfMissing = true)
public class BiddingRequesterServiceImpl implements BiddingRequesterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BiddingRequesterServiceImpl.class);

    private final int requestTimeOut;
    private final BidderRequester bidderRequester;

    public BiddingRequesterServiceImpl(final BidderRequester bidderRequester,
                                       @Value("${request.timeout}") final int requestTimeOut) {
        this.bidderRequester = bidderRequester;
        this.requestTimeOut = requestTimeOut;
    }

    @Override
    public Optional<BidderResponse> getBestBidderResponse(final Collection<String> bidderUrls,
                                                          final BidderRequest bidderRequest) {
        final Grinder grinder = new Grinder();

        final Map<String, CompletableFuture<BidderResponse>> futuresWithBidderResponseByBidder = bidderUrls
                .stream().collect(Collectors.toMap(bidder -> bidder, bidder -> bidderRequester.request(bidder, bidderRequest)));

        for (final String bidderUrl: bidderUrls) {
            try {
                final CompletableFuture<BidderResponse> future = futuresWithBidderResponseByBidder.get(bidderUrl);
                // we're syncing here (and caring about ordering, so response from the first request will be handled first)
                grinder.handleResponse(future.get(requestTimeOut, TimeUnit.SECONDS));
            } catch (TimeoutException timeoutException) {
                LOGGER.error("Request failed by timeout, bidder: " + bidderUrl, timeoutException);
            } catch (Exception exception) {
                LOGGER.error("Failed while executing request, bidder: " + bidderUrl, exception);
            }
        }

        return Optional.ofNullable(grinder.getBestBidderResponse());
    }
}
