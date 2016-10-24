package org.thiki.kanban.entrance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thiki.kanban.TestBase;
import org.thiki.kanban.foundation.annotations.Domain;
import org.thiki.kanban.foundation.annotations.Scenario;
import org.thiki.kanban.foundation.application.DomainOrder;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by xubt on 5/18/16.
 */
@Domain(order = DomainOrder.ENTRANCE, name = "入口")
@RunWith(SpringJUnit4ClassRunner.class)
public class EntranceControllerTest extends TestBase {
    @Scenario("初次访问系统时入口")
    @Test
    public void enter_shouldReturnEntranceSuccessfully() throws Exception {
        given().when()
                .get("/entrance")
                .then()
                .statusCode(200)
                .body("description", equalTo("Welcome!"))
                .body("_links.self.href", equalTo("http://localhost:8007/entrance"))
                .body("_links.self.methods.authPost.isAllowed", equalTo(false))
                .body("_links.self.methods.authGet.isAllowed", equalTo(true))
                .body("_links.self.methods.authPut.isAllowed", equalTo(false))
                .body("_links.self.methods.authDelete.isAllowed", equalTo(false))
                .body("_links.passwordRetrievalApplication.href", equalTo("http://localhost:8007/passwordRetrievalApplication"));
    }
}
