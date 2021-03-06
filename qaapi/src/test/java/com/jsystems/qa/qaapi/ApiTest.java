package com.jsystems.qa.qaapi;

import com.jsystems.qa.qaapi.database.UserDao;
import com.jsystems.qa.qaapi.model.azure.AzureAuthor;
import com.jsystems.qa.qaapi.model.device.User;
import com.jsystems.qa.qaapi.model.error.ErrorResponse;
import com.jsystems.qa.qaapi.model.user.MyUser;
import com.jsystems.qa.qaapi.model.user.UserAzure;
import com.jsystems.qa.qaapi.model.user.UserDb;
import com.jsystems.qa.qaapi.service.UserService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ApiTest")
@DisplayName("Api test")
public class ApiTest {

    @Test
    @DisplayName("First api test")
    public void firstApiTest() {
        RestAssured
                .given()
                .get("http://www.mocky.io/v2/5a6b69ec3100009d211b8aeb")
                .then()
                .assertThat()
                .statusCode(200)
                .body("name", equalTo("Piotr"))
                .body("surname", equalTo("Kowalski"));
    }

    @Test
    @DisplayName("Should returns correctly list of users")
    public void shouldReturnsListOfUsers() {
        RestAssured
                .given()
                .get("http://www.mocky.io/v2/5a6a58222e0000d0377a7789")
                .then()
                .assertThat()
                .statusCode(200)
                .body("[0].imie", notNullValue())
                .body("[0].imie", equalTo("Piotr"))
                .body("[0].nazwisko",notNullValue())
                .body("[0].nazwisko",equalTo("Kowalski"))
//                .body("[0].device[0].device.model.produce", equalTo("dell"))
        ;
    }

    @Test
    @DisplayName("Should returns correctly list of users using jsonPath mapping")
    public void jsonPathTest() {
        List<User> users = UserService.getUsers();

        assertTrue(users.get(0).imie.equals("Piotr"));
        assertTrue(users.get(0).nazwisko.equals("Kowalski"));
        assertTrue(users.get(0).device.get(0).type.equals("computer"));
        assertTrue(users.get(0).device.get(0).deviceModel.get(0).screenSize == 17);
        assertTrue(users.size() > 0);
    }

    @Test
    @DisplayName("Test with mapped of MyUser")
    public void myUserJsonPath() {
        MyUser myUserWithQuery = UserService.getUserWithQueryParam("Piotr", "Kowalski");
        MyUser myUserWitrhPathVariable = UserService.getUserWithPathVariable(2L,  5);
        MyUser myUser = UserService.getUser();

        assertThat(myUser.name).isEqualTo("Piotr");
        assertThat(myUser.surname).isEqualTo("Kowalski");

        assertThat(myUserWithQuery.name).isEqualTo("Piotr");
    }

    @Test
    @DisplayName("Post test")
    public void postTest() {
        String[] strings = UserService.postMyUser(new MyUser("Piotr", "Kowalski"));
        assertThat(strings).isEmpty();
    }

    @Test
    @DisplayName("Error response")
    public void errorResponse() {
        ErrorResponse errorResponse = UserService.getErrorResponse();

        assertThat(errorResponse.error.errorCode).isEqualTo(400);
        assertThat(errorResponse.error.validationError).isEqualTo("invalid_email");
        assertThat(errorResponse.error.message).isEqualTo("your email is invalid");
    }

    @Test
    public void azureUser(){

        UserAzure userAzure = UserService.getUserAzureById(1);

        assertThat(userAzure.id).isEqualTo(1);
        assertThat(userAzure.userName).isEqualTo("User 1");
        assertThat(userAzure.password).isEqualTo("Password1");
    }

    @Test
    @Disabled
    public void dbTest() {
        UserDb userDb = UserDao.getOneById(1L);
        assertThat(userDb.getName()).isEqualTo("Piotr");
    }

    @Test
    @DisplayName("Get azure authors")
    public void shouldReturnsAllAzureAuthorsList() {
        List<AzureAuthor> azureAuthors = UserService.getAzureAuthors();

        assertThat(azureAuthors.size()).isGreaterThan(0);
    }
}
