package org.thiki.kanban.sprint;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thiki.kanban.TestBase;
import org.thiki.kanban.foundation.annotations.Domain;
import org.thiki.kanban.foundation.annotations.Scenario;
import org.thiki.kanban.foundation.application.DomainOrder;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.StringEndsWith.endsWith;

/**
 * Created by xubt on 02/04/17.
 */
@Domain(order = DomainOrder.SPRINT, name = "迭代")
@RunWith(SpringJUnit4ClassRunner.class)
public class SprintControllerTest extends TestBase {

    @Scenario("当创建一个迭代时,如果参数合法,则创建成功并返回创建后的迭代信息")
    @Test
    public void shouldReturn201WhenCreateBoardSuccessfully() {
        given().header("userName", "someone")
                .body("{\"startTime\":\"2017-02-04 12:11:44\",\"endTime\":\"2017-02-05 12:11:44\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/boards/board-fooId/sprints")
                .then()
                .statusCode(201)
                .body("id", equalTo("fooId"))
                .body("startTime", equalTo("2017-02-04 12:11:44.000000"))
                .body("endTime", equalTo("2017-02-05 12:11:44.000000"))
                .body("status", equalTo(SprintCodes.IN_PROGRESS))
                .body("creationTime", notNullValue())
                .body("_links.board.href", endsWith("/boards/board-fooId"))
                .body("_links.self.href", endsWith("/boards/board-fooId/sprints/fooId"));
    }

    @Scenario("当创建一个迭代时,如果开始日期晚于结束日期,则不允许创建")
    @Test
    public void notAllowedIfStartTimeAfterEndTime() {
        given().header("userName", "someone")
                .body("{\"startTime\":\"2017-02-06 12:11:44\",\"endTime\":\"2017-02-05 12:11:44\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/boards/board-fooId/sprints")
                .then()
                .statusCode(400)
                .body("message", equalTo(SprintCodes.START_TIME_IS_AFTER_END_TIME.message()))
                .body("code", equalTo(SprintCodes.START_TIME_IS_AFTER_END_TIME.code()));
    }

    @Scenario("当创建一个迭代时,如果存在尚未归档的迭代,则不允许创建")
    @Test
    public void notAllowedIfUnArchivedSprintExist() {
        dbPreparation.table("kb_sprint").names("id,board_id,status").values("fooId", "board-fooId", 1).exec();
        given().header("userName", "someone")
                .body("{\"startTime\":\"2017-02-03 12:11:44\",\"endTime\":\"2017-02-05 12:11:44\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/boards/board-fooId/sprints")
                .then()
                .statusCode(400)
                .body("message", equalTo(SprintCodes.UNARCHIVE_SPRINT_EXIST.message()))
                .body("code", equalTo(SprintCodes.UNARCHIVE_SPRINT_EXIST.code()));
    }
}
