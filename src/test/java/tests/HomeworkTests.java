package tests;

import models.lombok.*;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static specs.RequestSpecs.*;
import static specs.ResponseSpecs.*;

public class HomeworkTests {
    @Test
    void singleUserTest() {
        String supportText = "To keep ReqRes free, contributions towards server costs are appreciated!";
        given()
                .spec(singleUserRequestSpec)
                .when()
                .get()
                .then()
                .spec(singleUserResponseSpec)
                .body("data.id", is(2),
                        "data.first_name", is("Janet"),
                        "support.text", is(supportText));
    }

    @Test
    void registerTest() {
        LoginBodyRegister login = new LoginBodyRegister();
        login.setEmail("eve.holt@reqres.in");
        login.setPassword("pistol");
        LoginResponseRegister response = given()
                .spec(registerRequestSpec)
                .body(login)
                .when()
                .post()
                .then()
                .spec(registerResponseSpec)
                .extract().as(LoginResponseRegister.class);

        assertThat(response.getId()).isEqualTo(4);
        assertThat(response.getToken()).isEqualTo("QpwL5tke4Pnpja7X4");
    }

    @Test
    void createTest() {
        CreateBody body = new CreateBody();
        body.setName("morpheus");
        body.setJob("leader");
        CreateResponseBody response = given()
                .spec(createRequestSpec)
                .body(body)
                .when()
                .post()
                .then()
                .spec(createResponseSpec)
                .extract().as(CreateResponseBody.class);

        assertThat(response.getName()).isEqualTo("morpheus");
        assertThat(response.getJob()).isEqualTo("leader");
    }

    @Test
    void registerUnsuccessfulTest() {
        LoginBodyRegisterUnsuccess email = new LoginBodyRegisterUnsuccess();
        email.setEmail("sydney@fife");
        LoginResponseRegisterUnsuccess response = given()
                .spec(registerRequestSpec)
                .when()
                .body(email)
                .post()
                .then()
                .spec(registerUnsuccessResponseSpec)
                .extract().as(LoginResponseRegisterUnsuccess.class);

        assertThat(response.getError()).isEqualTo("Missing password");
    }

    @Test
    void singleUserAvatarTest() {
        String avatarPath = "https://reqres.in/img/faces/2-image.jpg";
        given()
                .spec(singleUserRequestSpec)
                .when()
                .get()
                .then()
                .spec(singleUserResponseSpec)
                .body("data.avatar", is(avatarPath));
    }

}
