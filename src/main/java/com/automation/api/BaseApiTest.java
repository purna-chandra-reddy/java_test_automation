package com.automation.api;

import com.automation.config.ConfigManager;
import com.automation.listeners.TestListener;
import com.automation.reports.ExtentReportManager;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base API Test - Abstract class for API tests.
 * Provides common setup, teardown, and assertion methods for API testing.
 */
@Listeners(TestListener.class)
public abstract class BaseApiTest {

    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected final ConfigManager config = ConfigManager.getInstance();
    protected ApiClient apiClient;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("========== API TEST SUITE STARTED ==========");
        ExtentReportManager.initReports();
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        logger.info("Starting API test class: {}", this.getClass().getSimpleName());
        apiClient = new ApiClient();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        // Reset API client for each test if needed
        logger.debug("Preparing for API test method");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        logger.debug("API test method completed");
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        logger.info("Completed API test class: {}", this.getClass().getSimpleName());
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flushReports();
        logger.info("========== API TEST SUITE COMPLETED ==========");
    }

    // ==================== Assertion Helper Methods ====================

    /**
     * Assert response status code
     */
    protected void assertStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.getStatusCode())
                .as("Status code should be " + expectedStatusCode)
                .isEqualTo(expectedStatusCode);
        logger.info("Status code assertion passed: {}", expectedStatusCode);
    }

    /**
     * Assert response contains key
     */
    protected void assertResponseContainsKey(Response response, String key) {
        String value = response.jsonPath().getString(key);
        assertThat(value)
                .as("Response should contain key: " + key)
                .isNotNull();
        logger.info("Response contains key: {}", key);
    }

    /**
     * Assert response key equals value
     */
    protected void assertResponseKeyEquals(Response response, String key, Object expectedValue) {
        Object actualValue = response.jsonPath().get(key);
        assertThat(String.valueOf(actualValue))
                .as("Response key '" + key + "' should equal " + expectedValue)
                .isEqualTo(String.valueOf(expectedValue));
        logger.info("Response key '{}' equals expected value: {}", key, expectedValue);
    }

    /**
     * Assert response body contains text
     */
    protected void assertResponseBodyContains(Response response, String text) {
        assertThat(response.getBody().asString())
                .as("Response body should contain: " + text)
                .contains(text);
        logger.info("Response body contains: {}", text);
    }

    /**
     * Assert response time is less than threshold
     */
    protected void assertResponseTimeLessThan(Response response, long maxMilliseconds) {
        assertThat(response.getTime())
                .as("Response time should be less than " + maxMilliseconds + "ms")
                .isLessThan(maxMilliseconds);
        logger.info("Response time ({} ms) is less than {} ms", response.getTime(), maxMilliseconds);
    }

    /**
     * Assert response header exists
     */
    protected void assertHeaderExists(Response response, String headerName) {
        assertThat(response.getHeader(headerName))
                .as("Response should have header: " + headerName)
                .isNotNull();
        logger.info("Response has header: {}", headerName);
    }

    /**
     * Assert response header value
     */
    protected void assertHeaderValue(Response response, String headerName, String expectedValue) {
        assertThat(response.getHeader(headerName))
                .as("Header '" + headerName + "' should equal " + expectedValue)
                .isEqualTo(expectedValue);
        logger.info("Header '{}' equals: {}", headerName, expectedValue);
    }

    /**
     * Assert response content type
     */
    protected void assertContentType(Response response, String expectedContentType) {
        assertThat(response.getContentType())
                .as("Content-Type should contain: " + expectedContentType)
                .contains(expectedContentType);
        logger.info("Content-Type is: {}", response.getContentType());
    }

    /**
     * Assert response is JSON
     */
    protected void assertResponseIsJson(Response response) {
        assertContentType(response, "application/json");
    }

    /**
     * Assert array size in response
     */
    protected void assertArraySize(Response response, String jsonPath, int expectedSize) {
        java.util.List<?> list = response.jsonPath().getList(jsonPath);
        assertThat(list)
                .as("Array at '" + jsonPath + "' should have size " + expectedSize)
                .hasSize(expectedSize);
        logger.info("Array at '{}' has size: {}", jsonPath, expectedSize);
    }

    /**
     * Assert array is not empty
     */
    protected void assertArrayNotEmpty(Response response, String jsonPath) {
        java.util.List<?> list = response.jsonPath().getList(jsonPath);
        assertThat(list)
                .as("Array at '" + jsonPath + "' should not be empty")
                .isNotEmpty();
        logger.info("Array at '{}' is not empty", jsonPath);
    }

    /**
     * Get value from response
     */
    protected <T> T getResponseValue(Response response, String jsonPath) {
        return response.jsonPath().get(jsonPath);
    }

    /**
     * Log response details
     */
    protected void logResponse(Response response) {
        logger.info("Response Status Code: {}", response.getStatusCode());
        logger.info("Response Time: {} ms", response.getTime());
        logger.debug("Response Body: {}", response.getBody().asPrettyString());
    }





    //new code
    public static void verifyAssertEqual(String actual, String expected) throws AssertionError {
        Assertions.assertThat(expected).isEqualTo(actual);

    }

    public static void verifyAssertEqual(Object actual, Object expected) throws AssertionError {
        Assertions.assertThat(expected).isEqualTo(actual);
    }

    public static void verifyAssertNonEqual(String actual, String expected) throws AssertionError {
        Assertions.assertThat(expected).isNotEqualTo(actual);
    }

    public static void verifyAssertEqual(boolean actual, boolean expected) throws AssertionError {
        Assertions.assertThat(expected).isEqualTo(actual);
    }

    public static void verifyAssertEqual(BigDecimal actual, BigDecimal expected) throws AssertionError {
        Assertions.assertThat(expected).isEqualTo(actual);
    }

    public static void verifyAssertEqual(Integer actual, Integer expected) throws AssertionError {
        Assertions.assertThat(expected).isEqualTo(actual);
    }

    public static void verifyAssertEqual(int actual, int expected) throws AssertionError {
        Assertions.assertThat(expected).isEqualTo(actual);
    }

    public static void verifyAssertNonEqual(Integer actual, Integer expected) throws AssertionError {
        Assertions.assertThat(expected).isNotEqualTo(actual);
    }

    public static void verifyAssertNotNull(String expected) throws AssertionError {
        Assertions.assertThat(expected).isNotNull();
    }

    public static void verifyAssertNotNull(Object expected) throws AssertionError {
        Assertions.assertThat(expected).isNotNull();
    }

    public static void verifyAssertNull(String expected) throws AssertionError {
        Assertions.assertThat(expected).isNull();
    }

    public static void verifyAssertNull(Object expected) throws AssertionError {
        Assertions.assertThat(expected).isNull();
    }

    public static void verifyAssertFail(String message) throws AssertionError {
        Assertions.fail(message);
    }

    public static void verifyAssertContains(String value, String containsString) throws AssertionError {
        Assertions.assertThat(value).contains(containsString);
    }

    public static void verifyAssertNotContains(String value, String containsString) throws AssertionError {
        Assertions.assertThat(value).doesNotContain(containsString);
    }

    public static void verifyAssertTrue(Boolean condition){
        Assertions.assertThat(true).isEqualTo(condition);
    }

    public static void verifyAssertFalse(Boolean condition){
        Assertions.assertThat(false).isEqualTo(condition);
    }


    public static void compareMap(Map<Object, Object> actual, Map<Object, Object> expected) {
        verifyAssertEqual(actual.size(), expected.size());
        for (Object ch1 : actual.keySet()) {
            if (actual.get(ch1) != null && expected.get(ch1) != null) {
                Object actualValue = actual.get(ch1).toString();
                Object expectedValue = expected.get(ch1).toString();
                verifyAssertEqual(actualValue, expectedValue);
            }else if(actual.get(ch1)==null && expected.get(ch1)==null){
            }else{
                verifyAssertEqual(actual.get(ch1),expected.get(ch1));
            }
        }
    }

    public static void compareList(List<Object> actual, List<Object> expected){
        verifyAssertEqual(actual.size(), expected.size());
        Boolean flag = false;
        for (Object expectedEntry : expected) {
            List<Object> actualEntryCopy = new ArrayList<>(actual);
            for (Object actualEntry : actualEntryCopy) {
                try {
                    verifyAssertEqual(actualEntry, expectedEntry.toString());
                    flag = true;
                    actual.remove(actualEntry);
                    break;
                } catch (AssertionError e) {
                    flag = false;
                }
            }
            if (flag == false) {
                verifyAssertFail("Expected entry not found : " + expectedEntry);
            }
        }

    }
    
    
    public static void compareListOfObject(List<Object> actual, List<Object> expected){
    	if(actual.equals(expected)) {
            verifyAssertTrue(true);

    	}else {
            verifyAssertFail("Expected entry not found ");

    	}
    	
    }



    public static void compareMapofMap(Map<Object, Map<Object, Object>> actual, Map<Object, Map<Object, Object>> expected) {
        verifyAssertEqual(actual.size(), expected.size());
        for (Object ch1 : actual.keySet()) {
            if (actual.get(ch1) != null && expected.get(ch1) != null) {
                Map<Object, Object> myListVal = actual.get(ch1);
                Map<Object, Object> dbMapVal = expected.get(ch1);
                compareMap(myListVal, dbMapVal);
            }else if(actual.get(ch1)==null && expected.get(ch1)==null){
            }else{
                verifyAssertEqual(actual.get(ch1),expected.get(ch1));
            }
        }
    }

    public static void compareMapofList(Map<Object, List<Object>> actual, Map<Object, List<Object>> expected) {
        verifyAssertEqual(actual.size(), expected.size());
        for (Object ch1 : actual.keySet()) {
            if (actual.get(ch1) != null && expected.get(ch1) != null) {
                List<Object> myListVal = actual.get(ch1);
                List<Object> dbMapVal = expected.get(ch1);
                compareList(myListVal, dbMapVal);
            }else if(actual.get(ch1)==null && expected.get(ch1)==null){
            }else{
                verifyAssertEqual(actual.get(ch1),expected.get(ch1));
            }
        }
    }



    public static void compareListofMap(List<Map<Object, Object>> actual, List<Map<Object, Object>> expected) {
        verifyAssertEqual(actual.size(), expected.size());
        Boolean flag = false;
        for (Map<Object, Object> expectedEntry : expected) {
            List<Map<Object, Object>> actualEntryCopy = new ArrayList<>(actual);
            for (Map<Object, Object> actualEntry : actualEntryCopy) {
                try {
                    compareMap(actualEntry, expectedEntry);
                    flag = true;
                    actual.remove(actualEntry);
                    break;
                } catch (AssertionError e) {
                    flag = false;
                }
            }
            if (flag == false) {
                verifyAssertFail("Expected entry not found : " + expectedEntry);
            }
        }
    }

    public static void compareListofMapStringKey(List<Map<String, Object>> actual, List<Map<String, Object>> expected) {
        List<Map<Object,Object>> expectedUpdated = new LinkedList<>();
        for (Map<String, Object> expectedEntry : expected) {
            expectedUpdated.add(expectedEntry.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
        }
        List<Map<Object,Object>> actualUpdated = new LinkedList<>();
        for (Map<String, Object> actualEntry : actual) {
            actualUpdated.add(actualEntry.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
        }
        compareListofMap(actualUpdated,expectedUpdated);
    }


}
