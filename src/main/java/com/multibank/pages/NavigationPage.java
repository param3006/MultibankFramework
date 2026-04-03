package com.multibank.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Page Object encapsulating the shared top navigation component.
 *
 * <p>The nav is present on every page; reuse this PO via composition inside other POs or
 * directly in tests that focus purely on navigation behaviour.
 */
@Slf4j
public class NavigationPage extends BasePage {

    // ── Top bar ───────────────────────────────────────────────────────
    @FindBy(css = "header, nav, [class*='header'], [class*='topbar']")
    private WebElement header;

    @FindBy(css = "header a, nav a, [class*='nav-link'], [class*='menu-item'] a")
    private List<WebElement> allNavLinks;

    // ── Specific expected items ───────────────────────────────────────
    @FindBy(xpath = "//*[normalize-space(text())='Trade' or normalize-space(text())='TRADE']//ancestor-or-self::a")
    private WebElement tradeLink;

    @FindBy(xpath = "//*[normalize-space(text())='Spot' or normalize-space(text())='SPOT']//ancestor-or-self::a")
    private WebElement spotLink;

    @FindBy(xpath = "//*[normalize-space(text())='Convert' or normalize-space(text())='CONVERT']//ancestor-or-self::a")
    private WebElement convertLink;

    @FindBy(xpath = "//*[normalize-space(text())='Instant Buy' or normalize-space(text())='INSTANT BUY']//ancestor-or-self::a")
    private WebElement instantBuyLink;

    // ── Language selector ─────────────────────────────────────────────
    @FindBy(css = "[class*='lang'], [class*='locale'], select[name*='lang'], button[aria-label*='language']")
    private WebElement languageSelector;

    // ── Login / Sign-up buttons ───────────────────────────────────────
    @FindBy(css = "[class*='login'], [class*='sign-in'], a[href*='login'], button[class*='login']")
    private WebElement loginButton;

    @FindBy(css = "[class*='register'], [class*='sign-up'], a[href*='register'], button[class*='signup']")
    private WebElement registerButton;

    public boolean isHeaderVisible() {
        return isElementVisible(By.cssSelector("header, nav, [class*='header'], [class*='topbar']"));
    }

    /** Returns visible, distinct link texts in the nav. */
    public List<String> getAllNavLinkTexts() {
        log.info("Reading all nav link texts");
        return allNavLinks.stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    /** Returns href values for all nav links (for broken-link checks). */
    public List<String> getAllNavLinkHrefs() {
        return allNavLinks.stream()
                .map(el -> el.getAttribute("href"))
                .filter(h -> h != null && !h.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Looks for a nav link whose text equals {@code label} (case-insensitive).
     *
     * @return Optional containing the element if found
     */
    public Optional<WebElement> findNavLinkByText(String label) {
        return allNavLinks.stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(label))
                .findFirst();
    }

    /** Returns {@code true} if a nav link with the given text exists. */
    public boolean isNavItemPresent(String label) {
        return findNavLinkByText(label).isPresent();
    }

    /**
     * Clicks a nav item by visible text.
     *
     * @throws org.openqa.selenium.NoSuchElementException if not found
     */
    public void clickNavItem(String label) {
        log.info("Clicking nav item: '{}'", label);
        WebElement el = findNavLinkByText(label)
                .orElseThrow(() -> new org.openqa.selenium.NoSuchElementException(
                        "Nav item not found: '" + label + "'"));
        safeClick(el);
        waitForPageLoad();
    }

    // ── Specific known items ──────────────────────────────────────────

    public boolean isTradeItemPresent() {
        return isElementPresent(By.xpath(
            "//*[normalize-space(text())='Trade' or normalize-space(text())='TRADE']//ancestor-or-self::a"));
    }

    public boolean isSpotItemPresent() {
        return isElementPresent(By.xpath(
            "//*[normalize-space(text())='Spot' or normalize-space(text())='SPOT']//ancestor-or-self::a"));
    }

    public boolean isConvertItemPresent() {
        return isElementPresent(By.xpath(
            "//*[normalize-space(text())='Convert' or normalize-space(text())='CONVERT']//ancestor-or-self::a"));
    }

    public boolean isLanguageSelectorPresent() {
        return isElementPresent(By.cssSelector(
            "[class*='lang'], [class*='locale'], select[name*='lang'], button[aria-label*='language']"));
    }

    public boolean isLoginButtonPresent() {
        return isElementPresent(By.cssSelector(
            "[class*='login'], [class*='sign-in'], a[href*='login']"));
    }

    /** Checks the nav is fully functional: visible + contains expected link count. */
    public boolean isNavFullyRendered(int minimumLinks) {
        return isHeaderVisible() && getAllNavLinkTexts().size() >= minimumLinks;
    }

    @Override
    public boolean isPageLoaded() {
        return isHeaderVisible();
    }
}
