package api;

import static io.restassured.RestAssured.given;
import static tests.AllureTestopsTests.USER_TOKEN;

public class AuthorizationApi {
    public final static String
            ALLURE_TESTOPS_SESSION = "ALLURE_TESTOPS_SESSION";

    public String getXsrfToken(String userToken) {
        return given()
                .formParam("grant_type", "apitoken")
                .formParam("scope", "openid")
                .formParam("token", userToken)
                .when()
                .post("/api/uaa/oauth/token")
                .then()
                .statusCode(200)
                .extract()
                .path("jti");
    }

    public String getAuthorizationCookie(String userToken, String username, String password) {
        String xsrfToken = getXsrfToken(userToken);
        return given()
                .header("X-XSRF-TOKEN", xsrfToken)
                .header("Cookie", "XSRF-TOKEN=" + xsrfToken)
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/api/login/system")
                .then()
                .statusCode(200).extract().response()
                .getCookie(ALLURE_TESTOPS_SESSION);
    }

    public String getAuthorizationCookie(String userToken, String xsrfToken, String username, String password) {
        return given()
                .header("X-XSRF-TOKEN", xsrfToken)
                .header("Cookie", "XSRF-TOKEN=" + xsrfToken)
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/api/login/system")
                .then()
                .statusCode(200).extract().response()
                .getCookie(ALLURE_TESTOPS_SESSION);
    }
}
