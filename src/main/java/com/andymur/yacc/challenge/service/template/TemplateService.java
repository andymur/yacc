package com.andymur.yacc.challenge.service.template;

import com.andymur.yacc.challenge.model.BidderResponse;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TemplateService {

    private final Set<Resolver> resolvers = new HashSet<>();

    public TemplateService() {
        // this could be done via configuration with different type of resolvers
        resolvers.add(new BidPriceResolver());
    }

    public String resolveContent(final BidderResponse bidderResponse) {
        String content = bidderResponse.getContent();
        for (Resolver resolver: resolvers) {
            content = resolver.resolve(content, bidderResponse);
        }
        return content;
    }
}
