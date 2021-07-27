package com.andymur.yacc.challenge.service.bidding;

import com.andymur.yacc.challenge.model.BidderRequest;
import com.andymur.yacc.challenge.model.BidderResponse;
import com.andymur.yacc.challenge.model.ModelHelper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class BidderRequester {

    private static final Logger LOGGER = LoggerFactory.getLogger(BiddingRequesterServiceImpl.class);

    private final AsyncHttpClient httpClient;

    public BidderRequester(AsyncHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CompletableFuture<BidderResponse> request(final String bidderUrl,
                                              final BidderRequest bidderRequest) {
        LOGGER.info("request; sending the request: bidder={}, request={}", bidderUrl, bidderRequest);
        final Request request = new RequestBuilder(HttpMethod.POST.toString())
                .setUrl(bidderUrl)
                .setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(ModelHelper.createBidderRequest(bidderRequest)).build();

        return httpClient.executeRequest(request).toCompletableFuture().thenApplyAsync(response -> {
            String responseBody = response.getResponseBody();
            final BidderResponse bidderResponse = ModelHelper.createBidderResponse(responseBody);
            LOGGER.info("request; got response: bidder={}, response={}", bidderUrl, bidderResponse);
            return bidderResponse;
        });
    }
}
