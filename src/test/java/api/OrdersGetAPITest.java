package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import model.UserPOJO;

public class OrdersGetAPITest {
    private UserAPI userAPI;
    private boolean created;
    private String accessToken;
    private OrdersAPI ordersAPI;
    private Response orderResponse;

    @Before
    public void setup() {
        ordersAPI = new OrdersAPI();
    }

    @After
    public void teardown() {
        if (created) {
            Response deleteResponse = userAPI.sendDeleteUser(accessToken);
            boolean deleted = userDeletedSuccess(deleteResponse);
        }
    }

    //ПОЛУЧЕНИЕ ЗАКАЗОВ КОНКРЕТНОГО ПОЛЬЗОВАТЕЛЯ БЕЗ АВТОРИЗАЦИИ
    @Test
    @DisplayName("Проверка получения заказа конкретного пользователя без авторизации")
    public void createOrderWithoutAuthSuccess() {
        String expectedMessage = "You should be authorised";
        orderResponse = ordersAPI.sendGetOrdersWithoutAuth();
        String actualMessage = orderResponse.then()
                .assertThat()
                .statusCode(401)
                .extract()
                .path("message");
        ;
        Assert.assertEquals("Ожидается сообщение о том, что нужно быть авторизованным", expectedMessage, actualMessage);
    }

    //ПОЛУЧЕНИЕ ЗАКАЗОВ КОНКРЕТНОГО ПОЛЬЗОВАТЕЛЯ С АВТОРИЗАЦИЕЙ
    @Test
    @DisplayName("Проверка получения заказа конкретного пользователя с авторизацией")
    public void createOrderWithAuthSuccess() {
        userAPI = new UserAPI();
        UserPOJO user = UserPOJO.getRandom();
        Response response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        orderResponse = ordersAPI.sendGetOrdersWithAuth(accessToken);
        boolean orderGotten = orderResponse.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
        ;
        Assert.assertTrue("Ожидается, что будет получен список заказов конкретного пользователя", orderGotten);
    }


    @Step("Получить статус об успешном создании пользователя - 200")
    public boolean userCreatedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
    }

    @Step("Получить accessToken")
    public String userAccessToken(Response response) {
        return response.then()
                .extract()
                .path("accessToken");
    }

    @Step("Получить статус об успешном удалении пользователя - 202")
    public boolean userDeletedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(202)
                .extract()
                .path("success");
    }
}
