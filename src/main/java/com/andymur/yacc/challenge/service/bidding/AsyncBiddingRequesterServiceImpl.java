package com.andymur.yacc.challenge.service.bidding;

import com.andymur.yacc.challenge.model.BidderRequest;
import com.andymur.yacc.challenge.model.BidderResponse;
import com.andymur.yacc.challenge.model.ModelHelper;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.andymur.yacc.challenge.model.BidderResponse.FAILED_RESPONSE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Another way of implementation of BiddingRequesterService.
 * The crucial difference with BiddingRequesterService that here we don't pay attention to ordering of incoming responses
 * (first requested bidder could not be the first responded).
 *
 * First response with a highest bid wins.
 */
@Service
@ConditionalOnProperty(value = "biddingRequesterService.type", havingValue = "async", matchIfMissing = false)
public class AsyncBiddingRequesterServiceImpl implements BiddingRequesterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BiddingRequesterServiceImpl.class);

    private final AsyncHttpClient httpClient;
    // there is a difference how we treat timeout (with RequesterService)
    private final int requestTimeOut;

    public AsyncBiddingRequesterServiceImpl(final AsyncHttpClient httpClient,
                                            @Value("${request.timeout}") final int requestTimeOut) {
        this.httpClient = httpClient;
        this.requestTimeOut = requestTimeOut;
    }

    @Override
    public Optional<BidderResponse> getBestBidderResponse(final Collection<String> bidderUrls,
                                                          final BidderRequest bidderRequest) {
        final AsyncGrinder grinder = new AsyncGrinder(bidderUrls.size());

        for (final String bidderUrl: bidderUrls) {
            requestBidder(bidderUrl, bidderRequest, grinder);
        }

        try {
            /* pulling the first response with a highest bid value (we're syncing here with a timeout) */
            return grinder.getBestBidderResponseFuture().get(requestTimeOut, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            LOGGER.error("Request failed by timeout", timeoutException);
        } catch (Exception exception) {
            LOGGER.error("Failed while executing request", exception);
        }
        return Optional.empty();
    }

    private void requestBidder(final String bidderUrl,
                               final BidderRequest bidderRequest,
                               final AsyncGrinder grinder) {
        LOGGER.info("requestBidder; sending the request: bidder={}, request={}", bidderUrl, bidderRequest);

        final Request request = new RequestBuilder(HttpMethod.POST.toString())
                .setUrl(bidderUrl)
                .setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(ModelHelper.createBidderRequest(bidderRequest)).build();

        httpClient.executeRequest(request, new AsyncCompletionHandler<String>() {
            @Override
            public String onCompleted(Response response) {
                String responseBody = response.getResponseBody();
                final BidderResponse bidderResponse = ModelHelper.createBidderResponse(responseBody);
                LOGGER.info("onCompleted; request completed: bidder={}, responseBody={}", bidderUrl, bidderResponse);
                grinder.handleResponse(bidderResponse);
                return responseBody;
            }

            @Override
            public void onThrowable(Throwable t) {
                LOGGER.error("onThrowable; Failed while executing request", t);
                // otherwise we will fail after timeout with no clear answer
                grinder.handleResponse(FAILED_RESPONSE);
            }
        });
    }
}
