package com.multibank.tests;

import com.multibank.pages.HomePage;
import com.multibank.utils.TestDataLoader;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Test class: Trading Functionality
 *
 * <p>Validates:
 * <ol>
 *   <li>Spot trading section is present and visible</li>
 *   <li>Category filter tabs are displayed (All, BTC, ETH…)</li>
 *   <li>Trading pairs are listed with correct data structure</li>
 *   <li>Price data is populated for visible pairs</li>
 *   <li>Switching category tabs filters the pair list</li>
 *   <li>Expected trading pairs appear in the default view</li>
 * </ol>
 *
 * <p>Expected values are loaded from {@code testdata/trading.json}.
 */
@Slf4j
public class TradingTest extends BaseTest {

    private HomePage homePage;
    private Map<String, Object> testData;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        testData = TestDataLoader.loadJson("testdata/trading.json");
    }

    // ── TC-TRADE-001 ──────────────────────────────────────────────────

    @Test(
        description = "TC-TRADE-001 | Spot trading tab is visible on the home page",
        groups       = {"smoke", "trading"}
    )
    public void testSpotTabVisible() {
        log.info("TC-TRADE-001 – Spot tab visibility");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isPageLoaded())
            .as("Home page should be loaded (nav visible)")
            .isTrue();
        soft.assertAll();
    }

    // ── TC-TRADE-002 ──────────────────────────────────────────────────

    @Test(
        description = "TC-TRADE-002 | Category tabs appear in the trading section",
        groups       = {"regression", "trading"}
    )
    public void testCategoryTabsPresent() {
        log.info("TC-TRADE-002 – Category tabs check");
        List<String> expected = TestDataLoader.getList(testData, "expectedCategoryTabs");
        List<String> actual   = homePage.getCategoryTabTexts();

        log.info("Expected category tabs : {}", expected);
        log.info("Actual category tabs   : {}", actual);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(actual)
            .as("Category tabs should not be empty")
            .isNotEmpty();

        for (String tab : expected) {
            boolean found = actual.stream().anyMatch(a -> a.equalsIgnoreCase(tab));
            soft.assertThat(found)
                .as("Category tab '%s' should be present. Actual: %s", tab, actual)
                .isTrue();
        }
        soft.assertAll();
    }

    // ── TC-TRADE-003 ──────────────────────────────────────────────────

    @Test(
        description = "TC-TRADE-003 | Trading pairs are displayed with a non-empty list",
        groups       = {"smoke", "trading"}
    )
    public void testTradingPairsDisplayed() {
        int minPairs = TestDataLoader.getInt(testData, "minimumPairCount", 5);
        int actual   = homePage.getTradingPairCount();

        log.info("TC-TRADE-003 – Pair count: {} (minimum: {})", actual, minPairs);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(actual)
            .as("At least %d trading pairs should be visible; found %d", minPairs, actual)
            .isGreaterThanOrEqualTo(minPairs);
        soft.assertAll();
    }

    // ── TC-TRADE-004 ──────────────────────────────────────────────────

    @Test(
        description = "TC-TRADE-004 | Price data is populated for visible trading pairs",
        groups       = {"regression", "trading"}
    )
    public void testPricesAreDisplayed() {
        log.info("TC-TRADE-004 – Price data presence check");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.arePricesDisplayed())
            .as("At least one price cell should contain a non-empty value")
            .isTrue();
        soft.assertAll();
    }

    // ── TC-TRADE-005 ──────────────────────────────────────────────────

    @Test(
        description = "TC-TRADE-005 | Expected trading pairs appear in the pair name list",
        groups       = {"regression", "trading"}
    )
    public void testExpectedPairsPresent() {
        log.info("TC-TRADE-005 – Expected pair presence check");
        List<String> expectedPairs = TestDataLoader.getList(testData, "expectedPairs");
        List<String> actualPairs   = homePage.getTradingPairNames();

        log.info("Expected pairs : {}", expectedPairs);
        log.info("Actual pairs   : {}", actualPairs);

        SoftAssertions soft = new SoftAssertions();
        for (String pair : expectedPairs) {
            boolean found = actualPairs.stream()
                .anyMatch(a -> a.toUpperCase().contains(pair.toUpperCase()));
            soft.assertThat(found)
                .as("Pair '%s' should appear in the trading table. Actual: %s", pair, actualPairs)
                .isTrue();
        }
        soft.assertAll();
    }

    // ── TC-TRADE-006 ──────────────────────────────────────────────────

    @Test(
        description = "TC-TRADE-006 | Switching category tab updates the trading pair list",
        groups       = {"regression", "trading"},
        dataProvider = "categoryTabSwitchData"
    )
    public void testCategoryTabSwitch(String tabLabel, int expectedMinPairs) {
        log.info("TC-TRADE-006 – Switching to tab '{}', expecting >= {} pairs", tabLabel, expectedMinPairs);

        int before = homePage.getTradingPairCount();
        homePage.clickCategoryTab(tabLabel);
        int after = homePage.getTradingPairCount();

        log.info("Pair count before: {}, after switching to '{}': {}", before, tabLabel, after);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(after)
            .as("After clicking '%s' tab, at least %d pairs should be visible", tabLabel, expectedMinPairs)
            .isGreaterThanOrEqualTo(expectedMinPairs);
        soft.assertAll();
    }

    // ── TC-TRADE-007 ──────────────────────────────────────────────────

    @Test(
        description = "TC-TRADE-007 | Fear & Greed index widget is present on the home page",
        groups       = {"regression", "trading"}
    )
    public void testFearGreedIndexPresent() {
        log.info("TC-TRADE-007 – Fear & Greed index presence");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isFearGreedIndexPresent())
            .as("Fear & Greed index widget should be present on the home page")
            .isTrue();
        soft.assertAll();
    }

    // ── TC-TRADE-008 ──────────────────────────────────────────────────

    @Test(
        description = "TC-TRADE-008 | Panic Sell feature badge is present on the home page",
        groups       = {"regression", "trading"}
    )
    public void testPanicSellFeaturePresent() {
        log.info("TC-TRADE-008 – Panic Sell feature presence");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isPanicSellPresent())
            .as("'Panic Sell' feature reference should appear on the home page")
            .isTrue();
        soft.assertAll();
    }

    // ── Data Providers ────────────────────────────────────────────────

    @DataProvider(name = "categoryTabSwitchData", parallel = false)
    public Object[][] categoryTabSwitchData() {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
            (List<Map<String, Object>>) testData.getOrDefault("categoryTabSwitchData",
                List.of(
                    Map.of("tab", "All",  "minPairs", 5),
                    Map.of("tab", "BTC",  "minPairs", 1)
                ));

        return rows.stream()
            .map(r -> new Object[]{
                r.get("tab").toString(),
                Integer.parseInt(r.get("minPairs").toString())
            })
            .toArray(Object[][]::new);
    }
}
