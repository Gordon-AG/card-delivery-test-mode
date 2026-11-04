package ru.netology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;

class AuthTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Успешная авторизация активного пользователя")
    void shouldLoginAsActiveUser() {
        UserInfo user = DataGenerator.generateActiveUser();

        $("[data-test-id='login'] input").setValue(user.getLogin());
        $("[data-test-id='password'] input").setValue(user.getPassword());
        $("[data-test-id='action-login']").click();

        // 1. Проверяем, что адресная строка изменилась на /dashboard
        webdriver().shouldHave(url("http://localhost:9999/dashboard"), Duration.ofSeconds(15));

        // 2. И/или проверяем появление текста «Личный кабинет» на экране
        $("h2").shouldHave(exactText("Личный кабинет"));

    }

    @Test
    @DisplayName("Ошибка при авторизации заблокированного пользователя")
    void shouldShowErrorForBlockedUser() {
        UserInfo user = DataGenerator.generateBlockedUser();

        $("[data-test-id='login'] input").setValue(user.getLogin());
        $("[data-test-id='password'] input").setValue(user.getPassword());
        $("[data-test-id='action-login']").click();

        $("[data-test-id='error-notification']")
                .shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(exactText("Ошибка! Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Ошибка при неверном пароле для существующего логина")
    void shouldShowErrorForInvalidPassword() {
        UserInfo user = DataGenerator.generateActiveUser();
        UserInfo wrong = DataGenerator.withWrongPassword(user);

        $("[data-test-id='login'] input").setValue(user.getLogin());
        $("[data-test-id='password'] input").setValue(wrong.getPassword());
        $("[data-test-id='action-login']").click();

        $("[data-test-id='error-notification']")
                .shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(exactText("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Ошибка при несуществующем логине")
    void shouldShowErrorForNonExistentLogin() {
        UserInfo user = DataGenerator.generateNonExistentUser();

        $("[data-test-id='login'] input").setValue(user.getLogin());
        $("[data-test-id='password'] input").setValue(user.getPassword());
        $("[data-test-id='action-login']").click();

        $("[data-test-id='error-notification']")
                .shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(exactText("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Валидация: пустой логин")
    void shouldShowValidationErrorForEmptyLogin() {
        UserInfo user = DataGenerator.generateActiveUser();

        $("[data-test-id='password'] input").setValue(user.getPassword());
        $("[data-test-id='action-login']").click();

        $("[data-test-id='login']")
                .shouldBe(visible, Duration.ofSeconds(5))
                .find(".input__sub")
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    @DisplayName("Валидация: пустой пароль")
    void shouldShowValidationErrorForEmptyPassword() {
        UserInfo user = DataGenerator.generateActiveUser();

        $("[data-test-id='login'] input").setValue(user.getLogin());
        $("[data-test-id='action-login']").click();

        $("[data-test-id='password']")
                .shouldBe(visible, Duration.ofSeconds(5))
                .find(".input__sub")
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }
}