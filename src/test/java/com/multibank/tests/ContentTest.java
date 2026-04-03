package com.multibank.tests;

import com.multibank.pages.AboutPage;
import com.multibank.pages.HomePage;
import com.multibank.utils.TestDataLoader;
import com.multibank.utils.WaitUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

@Slf4j
public class ContentTest extends BaseTest {

    private HomePage homePage;
    private Map<String, Object> testData;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        testData = TestDataLoader.loadJson("testdata/content.json");
    }


    @Test(
        description = "TC-CONTENT-001 | Marketing banners are present at the page bottom",
        groups       = {"regression", "content"}
    )
    public void testMarketingBannersPresent() {
        int minBanners = TestDataLoader.getInt(testData, "minimumBannerCount", 1);
        int actual     = homePage.getMarketingBannerCount();

        log.info("TC-CONTENT-001 – Banner count: {} (minimum: {})", actual, minBanners);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(actual)
            .as("At least %d marketing banner(s) should be present; found %d", minBanners, actual)
            .isGreaterThanOrEqualTo(minBanners);
        soft.assertAll();
    }

    @Test(
        description = "TC-CONTENT-002 | App Store download link is present",
        groups       = {"smoke", "content"}
    )
    public void testAppStoreLinkPresent() {
        log.info("TC-CONTENT-002 – App Store link presence");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isAppStoreLinkPresent())
            .as("App Store link should be present in the download section")
            .isTrue();
        soft.assertAll();
    }


    @Test(
        description = "TC-CONTENT-003 | Google Play download link is present",
        groups       = {"smoke", "content"}
    )
    public void testGooglePlayLinkPresent() {
        log.info("TC-CONTENT-003 – Google Play link presence");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isGooglePlayLinkPresent())
            .as("Google Play link should be present in the download section")
            .isTrue();
        soft.assertAll();
    }


    @Test(
        description = "TC-CONTENT-004 | App Store link href points to Apple domain",
        groups       = {"regression", "content"}
    )
    public void testAppStoreLinkDestination() {
        String expectedDomain = TestDataLoader.getString(testData, "appStoreDomain");
        if (expectedDomain.isBlank()) expectedDomain = "apple.com";

        String actualHref = homePage.getAppStoreHref();
        log.info("TC-CONTENT-004 – App Store href: {}", actualHref);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(actualHref)
            .as("App Store href should point to '%s'; actual: %s", expectedDomain, actualHref)
            .containsIgnoringCase(expectedDomain);
        soft.assertAll();
    }

    @Test(
        description = "TC-CONTENT-005 | Google Play link href points to Google domain",
        groups       = {"regression", "content"}
    )
    public void testGooglePlayLinkDestination() {
        String expectedDomain = TestDataLoader.getString(testData, "googlePlayDomain");
        if (expectedDomain.isBlank()) expectedDomain = "play.google.com";

        String actualHref = homePage.getGooglePlayHref();
        log.info("TC-CONTENT-005 – Google Play href: {}", actualHref);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(actualHref)
            .as("Google Play href should point to '%s'; actual: %s", expectedDomain, actualHref)
            .containsIgnoringCase(expectedDomain);
        soft.assertAll();
    }


    @Test(
        description = "TC-CONTENT-006 | Download section is visible on scroll to bottom",
        groups       = {"regression", "content"}
    )
    public void testDownloadSectionVisible() {
        log.info("TC-CONTENT-006 – Download section visibility");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isDownloadSectionVisible())
            .as("Download section should be visible when scrolling to the page bottom")
            .isTrue();
        soft.assertAll();
    }


    @Test(
        description = "TC-CONTENT-007 | About Us page loads and main heading is present",
        groups       = {"regression", "content", "about"}
    )
    public void testAboutPageLoads() {
        String aboutUrl = TestDataLoader.getString(testData, "aboutPagePath");
        if (aboutUrl.isBlank()) aboutUrl = "https://trade.multibank.io/about";

        log.info("TC-CONTENT-007 – Navigating to About page: {}", aboutUrl);
        driver().get(aboutUrl);

        AboutPage aboutPage = new AboutPage();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(aboutPage.isPageLoaded())
            .as("About page should have a visible main heading after navigation")
            .isTrue();
        soft.assertAll();
    }


    @Test(
        description = "TC-CONTENT-008 | About page contains expected keywords and founding year",
        groups       = {"regression", "content", "about"}
    )
    public void testAboutPageKeywords() {
        String aboutUrl = TestDataLoader.getString(testData, "aboutPagePath");
        if (aboutUrl.isBlank()) aboutUrl = "https://trade.multibank.io/about";

        driver().get(aboutUrl);
        AboutPage aboutPage = new AboutPage();

        List<String> keywords = TestDataLoader.getList(testData, "aboutPageKeywords");
        log.info("TC-CONTENT-008 – Checking keywords: {}", keywords);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(aboutPage.pageContainsKeywords(keywords))
            .as("About page should contain all expected keywords: %s", keywords)
            .isTrue();
        soft.assertThat(aboutPage.containsFoundingYear())
            .as("About page should mention the founding year 2005")
            .isTrue();
        soft.assertAll();
    }


    @Test(
        description = "TC-CONTENT-009 | About page renders feature cards with text",
        groups       = {"regression", "content", "about"}
    )
    public void testAboutPageFeatureCards() {
        String aboutUrl = TestDataLoader.getString(testData, "aboutPagePath");
        if (aboutUrl.isBlank()) aboutUrl = "https://trade.multibank.io/about";

        driver().get(aboutUrl);
        AboutPage aboutPage = new AboutPage();
        int minCards = TestDataLoader.getInt(testData, "minimumFeatureCards", 2);

        log.info("TC-CONTENT-009 – Feature card count: {} (minimum: {})",
                 aboutPage.getFeatureCardCount(), minCards);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(aboutPage.getFeatureCardCount())
            .as("At least %d feature cards should be rendered", minCards)
            .isGreaterThanOrEqualTo(minCards);
        soft.assertAll();
    }


    @Test(
        description = "TC-CONTENT-010 | About page CTA button(s) are clickable",
        groups       = {"regression", "content", "about"}
    )
    public void testAboutPageCtaButtons() {
        String aboutUrl = TestDataLoader.getString(testData, "aboutPagePath");
        if (aboutUrl.isBlank()) aboutUrl = "https://trade.multibank.io/about";

        driver().get(aboutUrl);
        AboutPage aboutPage = new AboutPage();

        log.info("TC-CONTENT-010 – CTA button check");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(aboutPage.isCtaButtonClickable())
            .as("At least one CTA button on the About page should be enabled and visible")
            .isTrue();
        soft.assertAll();
    }


    @Test(
        description = "TC-CONTENT-011 | About page mentions trading volume",
        groups       = {"regression", "content", "about"}
    )
    public void testAboutPageTradingVolumeMentioned() {
        String aboutUrl = TestDataLoader.getString(testData, "aboutPagePath");
        if (aboutUrl.isBlank()) aboutUrl = "https://trade.multibank.io/about";

        driver().get(aboutUrl);
        AboutPage aboutPage = new AboutPage();

        log.info("TC-CONTENT-011 – Trading volume mention check");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(aboutPage.containsTradingVolume())
            .as("About page should mention a trading volume figure")
            .isTrue();
        soft.assertAll();
    }
}
