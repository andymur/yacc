package com.andymur.yacc.challenge.service.bidding;

import com.andymur.yacc.challenge.TestHelper;
import com.andymur.yacc.challenge.model.BidderRequest;
import com.andymur.yacc.challenge.model.BidderResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@ExtendWith(MockitoExtension.class)
class BiddingRequesterServiceTest {

    private BiddingRequesterServiceImpl biddingRequesterService;

    @Mock
    private BidderRequester requester;

    @BeforeEach
    public void setUp() {
        biddingRequesterService = new BiddingRequesterServiceImpl(requester, 1);
    }

    @Test
    public void testSelectBestBid() throws InterruptedException, ExecutionException, TimeoutException {
        List<String> biddingUrls = Arrays.asList("Google Cloud", "Azure", "AWS");

        final BidderRequest bidderRequest = TestHelper.createBidderRequest(1L,
                Collections.singletonList("a"), Collections.singletonList(5));

        Mockito.doReturn(createResponse(1L, 200, "We're Google!")).when(requester).request("Google Cloud", bidderRequest);
        Mockito.doReturn(createResponse(2L, 300, "We're MS!")).when(requester).request("Azure", bidderRequest);
        Mockito.doReturn(createResponse(3L, 400, "We're Amazon!")).when(requester).request("AWS", bidderRequest);

        final BidderResponse bestBidderResponse = biddingRequesterService.getBestBidderResponse(biddingUrls, bidderRequest)
                .orElse(BidderResponse.FAILED_RESPONSE);

        Assertions.assertEquals(400, bestBidderResponse.getBid());
        Assertions.assertEquals("We're Amazon!", bestBidderResponse.getContent());
    }

    @Test
    public void testSelectNoBid() throws InterruptedException, ExecutionException, TimeoutException {
        List<String> biddingUrls = Arrays.asList("Google Cloud", "Azure", "AWS");

        final BidderRequest bidderRequest = TestHelper.createBidderRequest(1L,
                Collections.singletonList("a"), Collections.singletonList(5));

        Mockito.doReturn(createNoResponseValue()).when(requester).request("Google Cloud", bidderRequest);
        Mockito.doReturn(createNoResponseValue()).when(requester).request("Azure", bidderRequest);
        Mockito.doReturn(createNoResponseValue()).when(requester).request("AWS", bidderRequest);

        final BidderResponse bestBidderResponse = biddingRequesterService.getBestBidderResponse(biddingUrls, bidderRequest)
                .orElse(BidderResponse.FAILED_RESPONSE);

        Assertions.assertEquals(BidderResponse.FAILED_RESPONSE, bestBidderResponse, "Best bidder response is empty (failed)");
    }

    private static CompletableFuture<BidderResponse> createResponse(long id, int bid, String content) {
        CompletableFuture<BidderResponse> response = new CompletableFuture<>();
        response.complete(new BidderResponse(id, bid, content));
        return response;
    }

    private static CompletableFuture<BidderResponse> createNoResponseValue() {
        return new CompletableFuture<>();
    }
}