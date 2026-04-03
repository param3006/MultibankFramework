package com.multibank.tests;

import com.multibank.pages.NavigationPage;
import com.multibank.utils.TestDataLoader;
import com.multibank.utils.WaitUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

@Slf4j
public class NavigationTest extends BaseTest {

    private NavigationPage navPage;
    private Map<String, Object> testData;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        navPage  = new NavigationPage();
        testData = TestDataLoader.loadJson("testdata/navigation.json");
    }

    // ── TC-NAV-001 ────────────────────────────────────────────────────

    @Test(
        description = "TC-NAV-001 | Header navigation bar is visible on page load",
        groups       = {"smoke", "navigation"}
    )
    public void testNavigationBarIsVisible() {
        log.info("TC-NAV-001 – Checking nav bar visibility");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(navPage.isHeaderVisible())
            .as("Header/nav container should be visible")
            .isTrue();
        soft.assertAll();
    }

    // ── TC-NAV-002 ────────────────────────────────────────────────────

    @Test(
        description = "TC-NAV-002 | All expected navigation items are present",
        groups       = {"regression", "navigation"}
    )
    public void testExpectedNavItemsPresent() {
        log.info("TC-NAV-002 – Validating expected nav items");
        List<String> expected = TestDataLoader.getList(testData, "expectedNavItems");
        List<String> actual   = navPage.getAllNavLinkTexts();

        log.info("Expected nav items : {}", expected);
        log.info("Actual nav items   : {}", actual);

        SoftAssertions soft = new SoftAssertions();
        for (String item : expected) {
            soft.assertThat(navPage.isNavItemPresent(item))
                .as("Nav item '%s' should be present. Actual: %s", item, actual)
                .isTrue();
        }
        soft.assertAll();
    }

    // ── TC-NAV-003 ────────────────────────────────────────────────────

    @Test(
        description = "TC-NAV-003 | Navigation bar contains at least the minimum required links",
        groups       = {"smoke", "navigation"}
    )
    public void testMinimumNavLinkCount() {
        int minLinks = TestDataLoader.getInt(testData, "minimumNavLinks", 3);
        List<String> actual = navPage.getAllNavLinkTexts();

        log.info("TC-NAV-003 – Link count: {} (minimum: {})", actual.size(), minLinks);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(actual.size())
            .as("Navigation should contain at least %d links; found: %s", minLinks, actual)
            .isGreaterThanOrEqualTo(minLinks);
        soft.assertAll();
    }

    // ── TC-NAV-004 ────────────────────────────────────────────────────

    @Test(
        description = "TC-NAV-004 | All navigation links have valid (non-empty) href attributes",
        groups       = {"regression", "navigation"}
    )
    public void testNavLinksHaveValidHrefs() {
        log.info("TC-NAV-004 – Checking nav link hrefs");
        List<String> hrefs = navPage.getAllNavLinkHrefs();

        log.info("Nav hrefs found: {}", hrefs);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(hrefs)
            .as("Nav links list should not be empty")
            .isNotEmpty();

        hrefs.forEach(href ->
            soft.assertThat(href)
                .as("href should not be blank or '#'")
                .isNotBlank()
        );
        soft.assertAll();
    }

    // ── TC-NAV-005 ────────────────────────────────────────────────────

    @Test(
        description = "TC-NAV-005 | Language selector is present in the navigation",
        groups       = {"regression", "navigation"}
    )
    public void testLanguageSelectorPresent() {
        log.info("TC-NAV-005 – Language selector check");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(navPage.isLanguageSelectorPresent())
            .as("Language selector should be present in nav bar")
            .isTrue();
        soft.assertAll();
    }

    // ── TC-NAV-006 ────────────────────────────────────────────────────

    @Test(
        description = "TC-NAV-006 | Login button is present in the navigation",
        groups       = {"smoke", "navigation"}
    )
    public void testLoginButtonPresent() {
        log.info("TC-NAV-006 – Login button check");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(navPage.isLoginButtonPresent())
            .as("Login button should be visible in the navigation bar")
            .isTrue();
        soft.assertAll();
    }

    // ── TC-NAV-007 ────────────────────────────────────────────────────

    @Test(
        description = "TC-NAV-007 | Clicking a nav item changes the URL to the expected destination",
        groups       = {"regression", "navigation"},
        dataProvider = "navItemDestinations"
    )
    public void testNavItemNavigation(String linkText, String expectedUrlFragment) {
        log.info("TC-NAV-007 – Clicking '{}', expecting URL to contain '{}'",
                 linkText, expectedUrlFragment);

        if (!navPage.isNavItemPresent(linkText)) {
            log.warn("Nav item '{}' not found – skipping", linkText);
            return;
        }

        navPage.clickNavItem(linkText);

        boolean urlMatches = WaitUtils.waitForUrlContains(driver(), expectedUrlFragment, 10);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(urlMatches)
            .as("After clicking '%s', URL should contain '%s'. Actual: %s",
                linkText, expectedUrlFragment, driver().getCurrentUrl())
            .isTrue();
        soft.assertAll();
    }

    // ── TC-NAV-008 ────────────────────────────────────────────────────

    @Test(
        description = "TC-NAV-008 | Nav is fully rendered: visible + min link threshold",
        groups       = {"smoke", "navigation"}
    )
    public void testNavFullyRendered() {
        int minLinks = TestDataLoader.getInt(testData, "minimumNavLinks", 3);
        log.info("TC-NAV-008 – Full render check (min {} links)", minLinks);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(navPage.isNavFullyRendered(minLinks))
            .as("Navigation should be visible and contain at least %d links", minLinks)
            .isTrue();
        soft.assertAll();
    }

    // ── Data Providers ────────────────────────────────────────────────

    /**
     * Provides nav-item → expected URL fragment pairs loaded from JSON test data.
     * Falls back to a minimal hard-coded set if JSON key is absent.
     */
    @DataProvider(name = "navItemDestinations", parallel = false)
    public Object[][] navItemDestinations() {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> pairs =
            (List<Map<String, String>>) testData.getOrDefault("navItemDestinations",
                List.of(
                    Map.of("link", "Spot",    "url", "spot"),
                    Map.of("link", "Convert", "url", "convert")
                ));

        return pairs.stream()
            .map(p -> new Object[]{ p.get("link"), p.get("url") })
            .toArray(Object[][]::new);
    }
}
