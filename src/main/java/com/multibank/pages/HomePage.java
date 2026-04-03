package com.multibank.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Page Object for the MultiBank trading platform home page (trade.multibank.io).
 *
 * <p>Covers:
 * <ul>
 *   <li>Top navigation bar</li>
 *   <li>Spot trading section with category tabs and trading pairs</li>
 *   <li>Marketing banners at the page bottom</li>
 *   <li>App download section (App Store / Google Play links)</li>
 * </ul>
 */
@Slf4j
public class HomePage extends BasePage {

    // ── URL constant ──────────────────────────────────────────────────
    public static final String RELATIVE_PATH = "/";

    // ── Navigation bar ────────────────────────────────────────────────
    /** The top-level navigation container */
    @FindBy(css = "nav, header nav, [class*='navbar'], [class*='header']")
    private WebElement navBar;

    /** All top-level nav link elements */
    @FindBy(css = "nav a, header nav a, [class*='nav-item'] a, [class*='navbar'] a")
    private List<WebElement> navLinks;

    /** Primary CTA / logo link */
    @FindBy(css = "a[href='/'], [class*='logo'] a, header [class*='brand'] a")
    private WebElement logoLink;

    // ── Spot trading section ──────────────────────────────────────────
    /** Section heading / tab for "Spot" trading */
    @FindBy(xpath = "//*[normalize-space(text())='Spot' or @data-tab='spot' or contains(@class,'spot')]")
    private WebElement spotTab;

    /** Category filter tabs inside the trading section (All, Favorites, BTC, ETH…) */
    @FindBy(css = "[class*='tab'] [class*='item'], [class*='category'] button, [class*='market-tab']")
    private List<WebElement> categoryTabs;

    /** Rows in the trading pair table */
    @FindBy(css = "table tbody tr, [class*='market-row'], [class*='pair-row'], [class*='trade-row']")
    private List<WebElement> tradingPairRows;

    /** Pair name / symbol cells */
    @FindBy(css = "[class*='pair-name'], [class*='symbol'], [class*='market-name']")
    private List<WebElement> pairNameCells;

    /** Price cells */
    @FindBy(css = "[class*='price'], [class*='last-price']")
    private List<WebElement> priceCells;

    // ── Marketing banners (bottom of page) ───────────────────────────
    @FindBy(css = "[class*='banner'], [class*='promo'], section[class*='marketing'], [class*='hero-bottom']")
    private List<WebElement> marketingBanners;

    // ── App download section ──────────────────────────────────────────
    @FindBy(css = "a[href*='apps.apple.com'], a[href*='itunes.apple.com'], [class*='app-store']")
    private WebElement appStoreLink;

    @FindBy(css = "a[href*='play.google.com'], [class*='google-play'], [class*='play-store']")
    private WebElement googlePlayLink;

    @FindBy(css = "[class*='download'], [class*='app-download'], section:has(a[href*='play.google.com'])")
    private WebElement downloadSection;

    // ── Panic Sell / feature badges ───────────────────────────────────
    @FindBy(xpath = "//*[contains(text(),'Panic Sell') or contains(text(),'panic-sell')]")
    private WebElement panicSellBadge;

    // ── MBG token banner ──────────────────────────────────────────────
    @FindBy(xpath = "//*[contains(text(),'MBG') or contains(@class,'mbg')]")
    private List<WebElement> mbgElements;

    // ── Fear & Greed index ────────────────────────────────────────────
    @FindBy(xpath = "//*[contains(text(),'Fear') or contains(text(),'Greed') or contains(@class,'fear')]")
    private WebElement fearGreedIndex;

    // ─────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────

    /** Checks that the navigation bar is rendered and visible. */
    public boolean isNavBarDisplayed() {
        return isElementVisible(By.cssSelector("nav, header nav, [class*='navbar'], [class*='header']"));
    }

    /**
     * Returns the visible text of every top-level nav link.
     * Only non-blank texts are returned.
     */
    public List<String> getNavLinkTexts() {
        log.info("Collecting navigation link texts");
        try {
            waitForAllVisible(By.cssSelector("nav a, header nav a, [class*='nav-item'] a"));
        } catch (Exception e) {
            log.warn("Timed out waiting for nav links; returning what is present");
        }
        return navLinks.stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public void clickNavLink(String linkText) {
        log.info("Clicking nav link: '{}'", linkText);
        WebElement target = navLinks.stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(linkText))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Nav link not found: '" + linkText + "'"));
        safeClick(target);
        waitForPageLoad();
    }

    public List<String> getNavLinkHrefs() {
        return navLinks.stream()
                .map(el -> el.getAttribute("href"))
                .filter(h -> h != null && !h.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public void clickSpotTab() {
        log.info("Clicking Spot tab");
        safeClick(spotTab);
        waitForPageLoad();
    }

    public List<String> getCategoryTabTexts() {
        return categoryTabs.stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isBlank())
                .collect(Collectors.toList());
    }

    public void clickCategoryTab(String label) {
        log.info("Clicking category tab: '{}'", label);
        categoryTabs.stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(label))
                .findFirst()
                .ifPresent(this::safeClick);
    }

    public int getTradingPairCount() {
        return tradingPairRows.size();
    }

    public List<String> getTradingPairNames() {
        return pairNameCells.stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isBlank())
                .collect(Collectors.toList());
    }

    public boolean arePricesDisplayed() {
        return priceCells.stream().anyMatch(el -> !el.getText().trim().isBlank());
    }

    public int getMarketingBannerCount() {
        scrollToBottom();
        return marketingBanners.size();
    }

    public boolean isAppStoreLinkPresent() {
        return isElementPresent(By.cssSelector(
            "a[href*='apps.apple.com'], a[href*='itunes.apple.com'], [class*='app-store']"));
    }

    public boolean isGooglePlayLinkPresent() {
        return isElementPresent(By.cssSelector(
            "a[href*='play.google.com'], [class*='google-play']"));
    }

    public String getAppStoreHref() {
        scrollIntoView(appStoreLink);
        return appStoreLink.getAttribute("href");
    }

    public String getGooglePlayHref() {
        scrollIntoView(googlePlayLink);
        return googlePlayLink.getAttribute("href");
    }

    public boolean isDownloadSectionVisible() {
        scrollToBottom();
        return isElementVisible(By.cssSelector(
            "[class*='download'], [class*='app-download']"));
    }

    // ── Misc features ─────────────────────────────────────────────────

    public boolean isPanicSellPresent() {
        return isElementPresent(
            By.xpath("//*[contains(text(),'Panic Sell') or contains(text(),'panic-sell')]"));
    }

    public boolean isFearGreedIndexPresent() {
        return isElementPresent(
            By.xpath("//*[contains(text(),'Fear') or contains(text(),'Greed') or contains(@class,'fear')]"));
    }

    // ── BasePage contract ─────────────────────────────────────────────

    @Override
    public boolean isPageLoaded() {
        return isNavBarDisplayed();
    }
}
