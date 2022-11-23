import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class HomeworkTests {
    @Test
    void singleUserTest() {
        String supportText = "To keep ReqRes free, contributions towards server costs are appreciated!";
        given()
                .log().uri()
                .when()
                .get("https://reqres.in/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.id", is(2),
                        "data.first_name", is("Janet"),
                        "support.text", is(supportText));
    }

    @Test
    void registerTest() {
        String data = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }";
        given()
                .log().uri()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("id", is(4),
                        "token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void createTest() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"leader\" }";
        given()
                .log().uri()
                .when()
                .contentType(ContentType.JSON)
                .body(data)
                .post("https://reqres.in/api/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("morpheus"));
    }

    @Test
    void registerUnsuccessfulTest() {
        String data = "{ \"email\": \"sydney@fife\" }";
        given()
                .log().uri()
                .when()
                .contentType(ContentType.JSON)
                .body(data)
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    void singleUserAvatarTest() {
        String avatarPath = "https://reqres.in/img/faces/2-image.jpg";
        given()
                .log().uri()
                .when()
                .get("https://reqres.in/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.avatar", is(avatarPath));
    }

}
