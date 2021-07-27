package com.andymur.yacc.challenge.controllers;

import com.andymur.yacc.challenge.TestHelper;
import com.andymur.yacc.challenge.configuration.BiddersConfiguration;
import com.andymur.yacc.challenge.model.BidderRequest;
import com.andymur.yacc.challenge.service.bidding.BiddingRequesterServiceImpl;
import com.andymur.yacc.challenge.service.template.TemplateService;
import org.asynchttpclient.AsyncHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.andymur.yacc.challenge.controllers.BiddingPlatformController.NO_BIDS_AVAILABLE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BiddingPlatformController.class)
class BiddingPlatformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BiddingRequesterServiceImpl biddingRequesterService;

    @MockBean
    private TemplateService templateService;

    @MockBean
    private AsyncHttpClient httpClient;

    @MockBean
    private BiddersConfiguration biddersConfiguration;

    @Value("${bidders}")
    private String bidders;

    @Test
    public void testHappyPath() throws Exception {
        final Collection<String> bidderUrls = bidderUrls(bidders);
        final BidderRequest bidderRequest = TestHelper.createBidderRequest(1L,
                Collections.singletonList("a"), Collections.singletonList(5));

        Mockito.when(biddersConfiguration.getBidderUrls()).thenReturn(bidderUrls(bidders));

        Mockito.when(biddingRequesterService.getBestBidderResponse(bidderUrls,
                bidderRequest)).thenReturn(
                Optional.empty()
        );

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/1?a=5")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(NO_BIDS_AVAILABLE))
                .andReturn();
    }

    private static Collection<String> bidderUrls(final String bidders) {
        return Arrays.asList(bidders.split(","));
    }
}