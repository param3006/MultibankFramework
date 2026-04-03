package com.multibank.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AboutPage extends BasePage {

    @FindBy(css = "h1, [class*='hero-title'], [class*='page-title']")
    private WebElement mainHeading;

    @FindBy(css = "h2, [class*='sub-heading'], [class*='section-title']")
    private List<WebElement> sectionHeadings;

    @FindBy(css = "[class*='stat'], [class*='metric'], [class*='number'], [class*='count']")
    private List<WebElement> statElements;

    @FindBy(css = "[class*='regulat'], [class*='authority'], [class*='license'], [alt*='ASIC'], [alt*='CySEC'], [alt*='BaFin']")
    private List<WebElement> regulationBadges;

    @FindBy(css = "[class*='feature-card'], [class*='trust-card'], [class*='why-card'], [class*='benefit']")
    private List<WebElement> featureCards;

    @FindBy(css = "a[class*='btn'], button[class*='cta'], [class*='start-trading']")
    private List<WebElement> ctaButtons;

    @FindBy(css = "[class*='timeline'], [class*='milestone'], [class*='history']")
    private List<WebElement> milestoneItems;

    @FindBy(css = "[class*='award'], [class*='trophy']")
    private List<WebElement> awardBadges;

    public String getMainHeadingText() {
        waitForVisible(mainHeading);
        return mainHeading.getText().trim();
    }


    public List<String> getSectionHeadingTexts() {
        return sectionHeadings.stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isBlank())
                .collect(Collectors.toList());
    }


    public int getStatCount() {
        return statElements.size();
    }


    public List<String> getStatTexts() {
        return statElements.stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isBlank())
                .collect(Collectors.toList());
    }


    public boolean hasRegulationBadges(int min) {
        log.info("Found {} regulation badges", regulationBadges.size());
        return regulationBadges.size() >= min;
    }


    public int getFeatureCardCount() {
        return featureCards.size();
    }


    public boolean allFeatureCardsHaveText() {
        return featureCards.stream().noneMatch(el -> el.getText().trim().isBlank());
    }


    public boolean containsFoundingYear() {
        return driver.getPageSource().contains("2005");
    }


    public boolean containsTradingVolume() {
        String src = driver.getPageSource().toLowerCase();
        return src.contains("billion") || src.contains("trading volume");
    }

    public int getCtaButtonCount() {
        return ctaButtons.size();
    }


    public boolean isCtaButtonClickable() {
        return ctaButtons.stream().anyMatch(el -> {
            try { return el.isEnabled() && el.isDisplayed(); }
            catch (Exception e) { return false; }
        });
    }


    public int getAwardCount() {
        return awardBadges.size();
    }

    public boolean pageContainsKeywords(List<String> keywords) {
        String src = driver.getPageSource().toLowerCase();
        return keywords.stream().allMatch(kw -> {
            boolean found = src.contains(kw.toLowerCase());
            if (!found) log.warn("Keyword NOT found on page: '{}'", kw);
            return found;
        });
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(By.cssSelector("h1, [class*='hero-title'], [class*='page-title']"));
    }
}
