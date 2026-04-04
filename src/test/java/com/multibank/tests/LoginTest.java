package com.multibank.tests;

import com.multibank.pages.LoginPage;
import com.multibank.utils.TestDataLoader;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

@Slf4j
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private Map<String, Object> testData;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        loginPage = new LoginPage();
        testData = TestDataLoader.loadJson("testdata/login.json");
    }


    @Test(
        description = "TC-CONTENT-001 | Perform Login",
        groups       = {"regression", "login"}
    )
    public void testMarketingBannersPresent() {
        loginPage.typeUserName(testData.get("username").toString());
    }


}
