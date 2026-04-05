package com.multibank.tests.DataProvider;

import com.multibank.models.LoginTestData;
import com.multibank.utils.TestDataLoader;
import org.testng.annotations.DataProvider;

public class DataProviders {

    @DataProvider(name ="loginData")
    public static Object[][] getLoginData(){
        try{
            LoginTestData data = TestDataLoader.loadLoginData("/Users/parampreetsingh/Downloads/multibank-qa/src/test/resources/testdata/login.json");
            Object[][] result = new Object[data.loginTests.size()][1];
            for(int i=0;i<data.loginTests.size();i++){
                result[i][0] = data.loginTests.get(i);
            }
            return result;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
