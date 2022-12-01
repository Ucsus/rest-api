package specs;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static helpers.CustomApiListener.withCustomTemplates;
import static io.restassured.RestAssured.with;

public class RequestSpecs {
    public static RequestSpecification registerRequestSpec = with()
            .filter(withCustomTemplates())
            .baseUri("https://reqres.in")
            .basePath("/api/register")
            .log().all()
            .contentType(ContentType.JSON);

    public static RequestSpecification singleUserRequestSpec = with()
            .filter(withCustomTemplates())
            .baseUri("https://reqres.in")
            .basePath("/api/users/2")
            .log().all()
            .contentType(ContentType.JSON);

    public static RequestSpecification createRequestSpec = with()
            .filter(withCustomTemplates())
            .baseUri("https://reqres.in")
            .basePath("/api/users")
            .log().all()
            .contentType(ContentType.JSON);

}
