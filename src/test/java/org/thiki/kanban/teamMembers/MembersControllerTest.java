package org.thiki.kanban.teamMembers;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thiki.kanban.TestBase;
import org.thiki.kanban.foundation.annotations.Domain;
import org.thiki.kanban.foundation.annotations.Scenario;
import org.thiki.kanban.foundation.application.DomainOrder;
import org.thiki.kanban.teams.team.TeamsCodes;
import org.thiki.kanban.teams.teamMembers.TeamMembersCodes;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by 濤 on 7/26/16.
 */
@Domain(order = DomainOrder.TEAM_MEMBER, name = "团队成员")
@RunWith(SpringJUnit4ClassRunner.class)
public class MembersControllerTest extends TestBase {
    @Scenario("加入一个团队")
    @Test
    public void joinTeam_shouldReturn201WhenJoinTeamSuccessfully() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_team (id,name,author) VALUES ('foo-teamId','team-name','someone')");
        given().header("userName", "someone")
                .body("{\"member\":\"someone\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/teams/foo-teamId/members")
                .then()
                .statusCode(201)
                .body("teamId", equalTo("foo-teamId"))
                .body("member", equalTo("someone"))
                .body("id", equalTo("fooId"))
                .body("_links.self.href", equalTo("http://localhost:8007/teams/foo-teamId/members"));
    }

    @Scenario("退出团队>用户加入某个团队后,可以选择离开团队")
    @Test
    public void leaveTeam() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_team (id,name,author) VALUES ('foo-teamId','team-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_team_members (id,team_id,member,author) VALUES ('foo-team-member-id','foo-teamId','someone','someone')");
        given().header("userName", "someone")
                .when()
                .delete("/teams/foo-teamId/members/someone")
                .then()
                .statusCode(200)
                .body("_links.teams.href", equalTo("http://localhost:8007/someone/teams"))
                .body("_links.self.href", equalTo("http://localhost:8007/teams/foo-teamId/members/someone"));
    }

    @Scenario("加入团队时,如果该团队并不存在,则不允许加入")
    @Test
    public void joinTeam_shouldReturnFailedIfTeamIsNotExist() throws Exception {
        given().header("userName", "someone")
                .body("{\"member\":\"someone\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/teams/foo-teamId/members")
                .then()
                .statusCode(400)
                .body("code", equalTo(TeamsCodes.TEAM_IS_NOT_EXISTS.code()))
                .body("message", equalTo(TeamsCodes.TEAM_IS_NOT_EXISTS.message()));
    }

    @Scenario("加入团队时,如果待加入的成员已经在团队中,则不允许加入")
    @Test
    public void joinTeam_shouldReturnFailedIfMemberIsAlreadyIn() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_team (id,name,author) VALUES ('foo-teamId','team-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_team_members (id,team_id,member,author) VALUES ('foo-team-member-id','foo-teamId','someone','someone')");
        given().header("userName", "someone")
                .body("{\"member\":\"someone\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/teams/foo-teamId/members")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("message", equalTo("Member named someone is already in the teams."));
    }

    @Scenario("当用户加入一个团队后，可以获取该团队的所有成员")
    @Test
    public void loadTeamMembersByTeamId() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_team (id,name,author) VALUES ('foo-teamId','team-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_team_members (id,team_id,member,author) VALUES ('foo-team-member-id','foo-teamId','someone','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,name,password) " +
                "VALUES ('fooUserId','someone@gmail.com','someone','password')");

        given().header("userName", "someone")
                .contentType(ContentType.JSON)
                .when()
                .get("/teams/foo-teamId/members")
                .then()
                .statusCode(200)
                .body("members[0].userName", equalTo("someone"))
                .body("members[0].email", equalTo("someone@gmail.com"))
                .body("members[0]._links.self.href", equalTo("http://localhost:8007/teams/foo-teamId/members/someone"))
                .body("members[0]._links.avatar.href", equalTo("http://localhost:8007/users/someone/avatar"))
                .body("members[0]._links.profile.href", equalTo("http://localhost:8007/users/someone/profile"))
                .body("_links.invitation.href", equalTo("http://localhost:8007/teams/foo-teamId/members/invitation"))
                .body("_links.member.href", equalTo("http://localhost:8007/teams/foo-teamId/members/someone"));
    }

    @Scenario("当用户加入一个团队后，可以获取该团队的所有成员。但是当团队不存在时,则不允许获取。")
    @Test
    public void NotAllowedIfTeamIsNotExitsWhenLoadingTeamMembersByTeamId() throws Exception {
        given().header("userName", "someone")
                .contentType(ContentType.JSON)
                .when()
                .get("/teams/foo-teamId/members")
                .then()
                .statusCode(400)
                .body("code", equalTo(TeamsCodes.TEAM_IS_NOT_EXISTS.code()))
                .body("message", equalTo(TeamsCodes.TEAM_IS_NOT_EXISTS.message()));
    }

    @Scenario("若当前用户并非团队成员，则不允许获取")
    @Test
    public void NotAllowedIfCurrentUserIsNotAMemberOfTheTeamWhenLoadingTeamMembersByTeamId() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_team (id,name,author) VALUES ('foo-teamId','team-name','someone')");
        given().header("userName", "someone")
                .contentType(ContentType.JSON)
                .when()
                .get("/teams/foo-teamId/members")
                .then()
                .statusCode(401)
                .body("code", equalTo(TeamMembersCodes.CURRENT_USER_IS_NOT_A_MEMBER_OF_THE_TEAM.code()))
                .body("message", equalTo(TeamMembersCodes.CURRENT_USER_IS_NOT_A_MEMBER_OF_THE_TEAM.message()));
    }
}
