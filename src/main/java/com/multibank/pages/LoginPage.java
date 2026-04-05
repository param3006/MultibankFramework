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

    private void typeUserName(String username){
        try {
            safeClick(userName);
            type(username,userName);
        } catch (ElementClickInterceptedException e) {
            jsClick(userName);
        }
    }

    private void typePassword(String password){
        try{
            safeClick(this.password);
            type(password,this.password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void clickLoginButton(){
        safeClick(loginBtn);
    }

    public void performLogin(String username,String password){
        typeUserName(username);
        typePassword(password);
        clickLoginButton();
    }
}
