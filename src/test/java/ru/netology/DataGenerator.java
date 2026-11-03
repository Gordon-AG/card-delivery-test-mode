package ru.netology;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private static final Faker faker = new Faker(new Locale("en"));
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private DataGenerator() {
    }

    public static UserInfo generateActiveUser() {
        return generateUser("active");
    }

    public static UserInfo generateBlockedUser() {
        return generateUser("blocked");
    }

    public static UserInfo generateUser(String status) {
        UserInfo user = new UserInfo(
                faker.name().username().toLowerCase(),
                faker.internet().password(),
                status
        );
        registerUser(user);
        return user;
    }

    public static void registerUser(UserInfo user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    public static UserInfo generateNonExistentUser() {
        return new UserInfo(
                faker.name().username().toLowerCase(),
                faker.internet().password(),
                "active"
        );
    }

    public static UserInfo withWrongPassword(UserInfo existingUser) {
        return new UserInfo(existingUser.getLogin(), faker.internet().password(), existingUser.getStatus());
    }
}