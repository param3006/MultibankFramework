package com.multibank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.NoSuchElementException;

public class LoginPage extends BasePage{

    @FindBy(xpath = "//input[@id=':r0:']")
    private WebElement userName;

    @FindBy(xpath = "//input[@id=':r1:']")
    private WebElement password;

    @FindBy(xpath = "//div[contains(text(),'Log In')]")
    private WebElement loginBtn;


    @Override
    public boolean isPageLoaded() {
        return isElementVisible(By.xpath("//span[contains(text(),'Log In')]"));
    }

    public void typeUserName(String text){
        try {
            safeClick(userName);
            type(text,userName);
        } catch (ElementClickInterceptedException e) {
            jsClick(userName);
        }

    }
}
