package ru.netology.testmode.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.testmode.data.DataGenerator;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.testmode.data.DataGenerator.Registration.getRegisteredUser;
import static ru.netology.testmode.data.DataGenerator.Registration.getUser;
import static ru.netology.testmode.data.DataGenerator.getRandomLogin;
import static ru.netology.testmode.data.DataGenerator.getRandomPassword;

class AuthTest {
    private static final String LOGIN_BUTTON_TEXT = "Продолжить";
    private static final String ACCOUNT_TEXT = "Личный кабинет";
    private static final String WRONG_LOGIN_PASSWORD_NOTIFICATION_TEXT = "Неверно указан логин или пароль";
    private static final String BLOCKED_USER_NOTIFICATION_TEXT = "Пользователь заблокирован";
    private static final String FIELD_MUST_BE_FILLED_TEXT = "Поле обязательно для заполнения";

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully login with active registered user")
    void shouldSuccessfulLoginIfRegisteredActiveUser() {
        var registeredUser = getRegisteredUser("active");
        fillAndSendLoginForm(registeredUser);
        $("h2").shouldHave(text(ACCOUNT_TEXT));
    }

    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser() {
        var notRegisteredUser = getUser("active");
        fillAndSendLoginForm(notRegisteredUser);
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text(WRONG_LOGIN_PASSWORD_NOTIFICATION_TEXT))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        var blockedUser = getRegisteredUser("blocked");
        fillAndSendLoginForm(blockedUser);
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text(BLOCKED_USER_NOTIFICATION_TEXT))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Should get error message if login with wrong login")
    void shouldGetErrorIfWrongLogin() {
        var registeredUser = getRegisteredUser("active");
        var wrongLogin = getRandomLogin();
        fillAndSendLoginForm(wrongLogin, registeredUser.getPassword());
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text(WRONG_LOGIN_PASSWORD_NOTIFICATION_TEXT))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Should get error message if login with wrong password")
    void shouldGetErrorIfWrongPassword() {
        var registeredUser = getRegisteredUser("active");
        var wrongPassword = getRandomPassword();
        fillAndSendLoginForm(registeredUser.getLogin(), wrongPassword);
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text(WRONG_LOGIN_PASSWORD_NOTIFICATION_TEXT))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Should get warning if login with empty login")
    void shouldGetWarningIfEmptyLogin() {
        var registeredUser = getRegisteredUser("active");
        fillAndSendLoginForm("", registeredUser.getPassword());
        $("[data-test-id=login].input_invalid .input__sub")
                .shouldHave(text(FIELD_MUST_BE_FILLED_TEXT));
    }

    @Test
    @DisplayName("Should get warning if login with empty password")
    void shouldGetWarningIfEmptyPassword() {
        var registeredUser = getRegisteredUser("active");
        fillAndSendLoginForm(registeredUser.getLogin(), "");
        $("[data-test-id=password].input_invalid .input__sub")
                .shouldHave(text(FIELD_MUST_BE_FILLED_TEXT));
    }

    @Test
    @DisplayName("Should get warning if login with empty login and password")
    void shouldGetWarningIfEmptyLoginAndPassword() {
        fillAndSendLoginForm("", "");
        $("[data-test-id=login].input_invalid .input__sub")
                .shouldHave(text(FIELD_MUST_BE_FILLED_TEXT));
        $("[data-test-id=password].input_invalid .input__sub")
                .shouldHave(text(FIELD_MUST_BE_FILLED_TEXT));
    }

    private void fillAndSendLoginForm(DataGenerator.RegistrationDto user) {
        fillAndSendLoginForm(user.getLogin(), user.getPassword());
    }

    private void fillAndSendLoginForm(String login, String password) {
        fillLogin(login);
        fillPassword(password);
        sendForm();
    }

    private void fillLogin(String login) {
        $("[data-test-id=login] input[name=login]").setValue(login);
    }

    private void fillPassword(String password) {
        $("[data-test-id=password] input[name=password]").setValue(password);
    }

    private void sendForm() {
        $("[data-test-id=action-login].button").shouldHave(text(LOGIN_BUTTON_TEXT)).click();
    }
}
