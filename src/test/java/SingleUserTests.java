import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class SingleUserTests {
    /*
        1. Make request to https://reqres.in/api/users/2
        2. Get response { data: { id: 2,
        email: "janet.weaver@reqres.in",
        first_name: "Janet",
        last_name: "Weaver",
        avatar: "https://reqres.in/img/faces/2-image.jpg" },
        support: { url: "https://reqres.in/#support-heading",
        text: "To keep ReqRes free, contributions towards server costs are appreciated!" } }
        3. Check first_name is Janet
    */
    @Test
    void checkFirstNameTest() {
        given()
                .log().uri()
                .when()
                .get("https://reqres.in/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.first_name", is("Janet"));
    }

    @Test
    void checkEmailTest() {
        given()
                .log().uri()
                .when()
                .get("https://reqres.in/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.email", is("janet.weaver@reqres.in"));
    }

    @Test
    void check404Test() {
        given()
                .log().uri()
                .when()
                .get("https://reqres.in/api/users/20")
                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    void wrongSupportTextTest() {
        given()
                .log().uri()
                .when()
                .get("https://reqres.in/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("support.text", not("That rug really tied the room together."));
    }

    @Test
    void checkAvatarTest() {
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
