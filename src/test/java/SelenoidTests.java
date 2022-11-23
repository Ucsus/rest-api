import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelenoidTests {
    /*
        1. Make request to https://selenoid.autotests.cloud/status
        2. Get response { total: 20, used: 1, queued: 0, pending: 0, browsers: { android: { 8.1: { } },
            chrome: { 100.0: { user1: { count: 1, sessions: [ { id: "463157db7b8fa59258a211a742171b63", container: "eb00cb6c9db5ac9774503647295e9bd1945f7b2d70a715b4de86c5f3508ad848", containerInfo: { id: "eb00cb6c9db5ac9774503647295e9bd1945f7b2d70a715b4de86c5f3508ad848", ip: "172.18.0.4" }, vnc: true, screen: "1920x1080x24", caps: { browserName: "chrome", version: "100.0", browserVersion: "100.0", screenResolution: "1920x1080x24", enableVNC: true, enableVideo: true, videoName: "selenoid9e2b5084cb40750a37cbb749c1f9f55a.mp4", videoScreenSize: "1920x1080" }, started: "2022-11-22T16:16:10.565353058Z" } ] } }, 99.0: { } },
            chrome-mobile: { 86.0: { } },
            firefox: { 97.0: { }, 98.0: { } },
            opera: { 84.0: { }, 85.0: { } } } }
        3. Check total is 20
     */
    @Test
    void checkTotal() {
        get("https://selenoid.autotests.cloud/status")
                .then()
                .body("total", is(20));
    }

    @Test
    void checkTotalStatus() {
        get("https://selenoid.autotests.cloud/status")
                .then()
                .statusCode(200)
                .body("total", is(20));
    }

    @Test
    void checkTotalWithGiven() {
        given()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .statusCode(200)
                .body("total", is(20));
    }

    @Test
    void checkTotalWithLogs() {
        given()
                .log().all()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().all()
                .statusCode(200)
                .body("total", is(20));
    }

    @Test
    void checkTotalWithSomeLogs() {
        given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("total", is(20));
    }

    @Test
    void checkChromeVersion() {
        given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("browsers.chrome", hasKey("100.0"));
    }

    @Test
    void checkResponsGoodPractice() {
        Integer expectedTotal = 20;

        Integer actualTotal = given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().path("total");

        assertEquals(expectedTotal, actualTotal);
    }

    /*
        1. Make request to https://selenoid.autotests.cloud/wd/hub/status
        2. Get response { value: { message: "Selenoid 1.10.7 built at 2021-11-21_05:46:32AM", ready: true } }
        3. Check value.ready is true
    */
    @Test
    void checkWdHubStatus401() {
        given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/wd/hub/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(401);
    }

    @Test
    void checkWdHubStatus() {
        given()
                .log().uri()
                .when()
                .get("https://user1:1234@selenoid.autotests.cloud/wd/hub/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("value.ready", is(true));
    }

    @Test
    void checkWdHubWithAuthStatus() {
        given()
                .log().uri()
                .auth().basic("user1", "1234")
                .when()
                .get("https://selenoid.autotests.cloud/wd/hub/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("value.ready", is(true));
    }

}
