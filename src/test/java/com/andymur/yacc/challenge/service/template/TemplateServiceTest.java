package com.andymur.yacc.challenge.service.template;

import com.andymur.yacc.challenge.model.BidderResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemplateServiceTest {

    private TemplateService templateService;

    @BeforeEach
    public void setUp() {
        templateService = new TemplateService();
    }

    @Test
    public void testTemplateWithPrice() {
        final String resolvedContent = templateService.resolveContent(
                new BidderResponse(1L, 500, "Hello, our price is really good, it's only $ $price$"));
        Assertions.assertEquals("Hello, our price is really good, it's only $ 500",
                resolvedContent, "Content value doesn't equal to expected one");
    }

    @Test
    public void testTemplateWithoutPrice() {
        final String resolvedContent = templateService.resolveContent(
                new BidderResponse(1L, 500, "Hello, our price is really good, it's only $ $oops$"));
        Assertions.assertEquals("Hello, our price is really good, it's only $ $oops$",
                resolvedContent, "Content value doesn't equal to expected one");
    }
}