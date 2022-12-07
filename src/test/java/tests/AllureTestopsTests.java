package tests;

import api.AuthorizationApi;
import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import models.lombok.CreateTestCaseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static api.AuthorizationApi.ALLURE_TESTOPS_SESSION;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static helpers.CustomApiListener.withCustomTemplates;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class AllureTestopsTests {
    public final static String
            USERNAME = "allure8",
            PASSWORD = "allure8",
            USER_TOKEN = "aa18ae38-a1d9-43c3-8701-6f90a35ff63b"; // create it in allure_url//user/30

    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = "https://allure.autotests.cloud";
        RestAssured.baseURI = "https://allure.autotests.cloud";
        RestAssured.filters(withCustomTemplates());
    }

    @Test
    void loginTest() {
        open("");
        $(byName("username")).setValue(USERNAME);
        $(byName("password")).setValue(PASSWORD).pressEnter();

        $("button[aria-label=\"User menu\"]").click();
        $(".Menu__item_info").shouldHave(text(USERNAME));
    }

    @Test
    void loginWithApiTest() {
        String xsrfToken = given()
                .formParam("grant_type", "apitoken")
                .formParam("scope", "openid")
                .formParam("token", USER_TOKEN)
                .when()
                .post("/api/uaa/oauth/token")
                .then()
                .statusCode(200)
                .extract()
                .path("jti");

        String authorizationCookie = given()
                .header("X-XSRF-TOKEN", xsrfToken)
                .header("Cookie", "XSRF-TOKEN=" + xsrfToken)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .when()
                .post("/api/login/system")
                .then()
                .statusCode(200).extract().response()
                .getCookie(ALLURE_TESTOPS_SESSION);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("");
        $("button[aria-label=\"User menu\"]").click();
        $(".Menu__item_info").shouldHave(text(USERNAME));
    }

    @Test
    void loginWithApiSimpleTest() {
        String authorizationCookie = new AuthorizationApi().getAuthorizationCookie(USER_TOKEN, USERNAME, PASSWORD);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("");
        $("button[aria-label=\"User menu\"]").click();
        $(".Menu__item_info").shouldHave(text(USERNAME));
    }

    @Test
    void viewTestCaseWithApiTest() {
        /*
        1. Make GET request to /api/rs/testcase/13328/overview
        2. Check name is "View test case name"
        */
        String authorizationCookie = new AuthorizationApi()
                .getAuthorizationCookie(USER_TOKEN, USERNAME, PASSWORD);

        given()
                .log().all()
                .cookie(ALLURE_TESTOPS_SESSION, authorizationCookie)
                .get("/api/rs/testcase/13328/overview")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is("View test case name"));
    }

    @Test
    void viewTestCaseWithUiTest() {
        /*
        1. Open page /project/1722/test-cases/13328
        2. Check name is "View test case name"
        */
        String authorizationCookie = new AuthorizationApi()
                .getAuthorizationCookie(USER_TOKEN, USERNAME, PASSWORD);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("/project/1722/test-cases/13328");
        $(".TestCaseLayout__name").shouldHave(text("View test case name"));

    }

    @Test
    void createTestCaseWithApiTest() {
        /*
        1. Make POST request to /api/rs/testcasetree/leaf?projectId=1722
        with body {"name": "Another some random test"}
        2. Get test_case {id} from response
        {"id":13514,"name":"Another some random test","automated":false,"external":false,"createdDate":1670414770345,"statusName":"Draft","statusColor":"#abb8c3"}
        3. Open page /project/1722/test-cases/{id}
        2. Check name is "Another some random test"
        */
        AuthorizationApi authorizationApi = new AuthorizationApi();

        String xsrfToken = authorizationApi.getXsrfToken(USER_TOKEN);
        String authorizationCookie = authorizationApi
                .getAuthorizationCookie(USER_TOKEN, xsrfToken, USERNAME, PASSWORD);

        Faker faker = new Faker();
        String testCaseName = faker.name().nameWithMiddle();

        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);
        //        String testCaseBody = "\"name\": \"Another some random test\"";

        int testCaseId = given()
                .log().all()
                .header("X-XSRF-TOKEN", xsrfToken)
                .cookies("XSRF-TOKEN", xsrfToken,
                        ALLURE_TESTOPS_SESSION, authorizationCookie)
                .body(testCaseBody)
                .contentType(ContentType.JSON)
                .queryParam("projectId", "1722")
                .post("/api/rs/testcasetree/leaf")
                //                .post("/api/rs/testcasetree/leaf?projectId=1722")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", is(testCaseName))
                .body("automated", is(false))
                .body("external", is(false))
                .extract()
                .path("id");

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));
        open("/project/1722/test-cases/" + testCaseId);
        $(".TestCaseLayout__name").shouldHave(text(testCaseName));
    }
}
