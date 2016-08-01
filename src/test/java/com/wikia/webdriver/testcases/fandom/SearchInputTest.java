package com.wikia.webdriver.testcases.fandom;

import com.gargoylesoftware.htmlunit.javascript.host.URL;
import com.wikia.webdriver.common.core.Assertion;
import com.wikia.webdriver.common.templates.fandom.FandomTestTemplate;
import com.wikia.webdriver.elements.fandom.components.SearchInput;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by psko on 8/1/16.
 */

@Test(groups = {"Fandom", "Fandom_Search"})
public class SearchInputTest extends FandomTestTemplate {

    @Test
    public void searchInputAcceptsValue() {
        SearchInput searchInput = new SearchInput().searchInputValue();
        SearchInput searchInputSubmit = new SearchInput().searchInputSubmit();

        String URL = driver.getCurrentUrl();
        Assert.assertEquals(URL, "http://qa.fandom.wikia.com/?s=TEST");

    }
}

//Assertion.assertTrue(driver.getCurrentUrl().contains(MercurySubpages.JOIN_PAGE));