package com.andymur.yacc.challenge.controllers;

import com.andymur.yacc.challenge.configuration.BiddersConfiguration;
import com.andymur.yacc.challenge.model.BidderRequest;
import com.andymur.yacc.challenge.model.BidderResponse;
import com.andymur.yacc.challenge.service.bidding.BiddingRequesterService;
import com.andymur.yacc.challenge.service.template.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class BiddingPlatformController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BiddingPlatformController.class);
	public static final String NO_BIDS_AVAILABLE = "NO_BIDS_AVAILABLE";

	private final BiddersConfiguration biddersConfiguration;
	private final BiddingRequesterService biddingRequesterService;

	private final TemplateService templateService;

	public BiddingPlatformController(final BiddersConfiguration biddersConfiguration,
									 final BiddingRequesterService biddingRequesterService,
									 final TemplateService templateService) {
		this.biddingRequesterService = biddingRequesterService;
		this.biddersConfiguration = biddersConfiguration;
		this.templateService = templateService;
	}

	@GetMapping("/{id}")
	public String biddersRequest(@PathVariable long id,
								 @RequestParam Map<String, String> attributes) {
		final Map<String, Integer> requestAttributes = new HashMap<>();

		attributes.forEach((key, value) -> requestAttributes.put(key, Integer.valueOf(value)));
		final BidderRequest bidderRequest = new BidderRequest(id, requestAttributes);
		final Collection<String> bidders = biddersConfiguration.getBidderUrls();
		LOGGER.info("biddersRequest.start; bidders={}, request={}", bidders, bidderRequest);

		final Optional<BidderResponse> bestBidderResponse = biddingRequesterService.getBestBidderResponse(bidders, bidderRequest);
		final String bestBidderContent = bestBidderResponse.map(templateService::resolveContent).orElse(NO_BIDS_AVAILABLE);
		LOGGER.info("biddersRequest.end; bestBidderContent = {}", bestBidderContent);
		return bestBidderContent;
	}
}
