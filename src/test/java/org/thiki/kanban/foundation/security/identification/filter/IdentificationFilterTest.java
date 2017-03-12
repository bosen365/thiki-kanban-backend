package org.thiki.kanban.foundation.security.identification.filter;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.thiki.kanban.IdentificationTestBase;
import org.thiki.kanban.foundation.annotations.Scenario;
import org.thiki.kanban.foundation.common.date.DateService;
import org.thiki.kanban.foundation.common.date.DateStyle;
import org.thiki.kanban.foundation.security.Constants;
import org.thiki.kanban.foundation.security.identification.rsa.RSAService;
import org.thiki.kanban.foundation.security.identification.token.AuthenticationToken;
import org.thiki.kanban.foundation.security.identification.token.TokenService;

import javax.annotation.Resource;
import java.util.Date;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by xubt on 7/10/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class IdentificationFilterTest extends IdentificationTestBase {
    @Resource
    private RSAService rsaService;
    @Resource
    private DateService dateService;
    @Resource
    private TokenService tokenService;


    @Scenario("当请求需要认证时,如果没有携带token,则告知客户端需要授权")
    @Test
    public void shouldReturn401WhenAuthIsRequired() throws Exception {
        given().when()
                .get("/someone/projects/project-fooId/boards")
                .then()
                .statusCode(401)
                .body("code", equalTo(Constants.SECURITY_IDENTITY_NO_AUTHENTICATION_TOKEN_CODE))
                .body("message", equalTo(Constants.SECURITY_IDENTITY_NO_AUTHENTICATION_TOKEN));
    }

    @Scenario("如果用户在5分钟内未发送请求,token将会失效,告知客户端需要重新授权")
    @Test
    public void shouldReturnTimeOut() throws Exception {
        String userName = "foo";
        String expiredToken = buildToken(userName, new Date(), -5);
        given().header("token", expiredToken)
                .when()
                .get("/someone/projects/project-fooId/boards")
                .then()
                .statusCode(401)
                .body("code", equalTo(Constants.SECURITY_IDENTITY_AUTHENTICATION_TOKEN_HAS_EXPIRE_CODE))
                .body("message", equalTo(Constants.SECURITY_IDENTITY_AUTHENTICATION_TOKEN_HAS_EXPIRE));
    }

    @Scenario("当token不为空且未失效时,请求到达后更新token的有效期")
    @Test
    public void shouldUpdateTokenExpiredTime() throws Exception {
        String name = "foo";
        String currentToken = buildToken(name, new Date(), 2);
        Date newExpiredTime = dateService.addMinute(new Date(), Constants.TOKEN_EXPIRED_TIME);

        dateService = spy(dateService);
        when(dateService.addMinute(any(Date.class), eq(Constants.TOKEN_EXPIRED_TIME))).thenReturn(newExpiredTime);
        ReflectionTestUtils.setField(tokenService, "dateService", dateService);

        given().header("userName", name)
                .header("token", currentToken)
                .body("{\"name\":\"newSummary\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/someone/projects/project-fooId/boards")
                .then()
                .header("token", notNullValue());
    }

    @Scenario("当token中的用户名与header中携带的用户名不一致时,告知客户端认证未通过")
    @Test
    public void shouldAuthenticatedFailedWhenUserNameIsNotConsistent() throws Exception {
        String userName = "foo";
        String tamperedUserName = "fee";
        String expiredToken = buildToken(userName, new Date(), 2);
        given().header("userName", tamperedUserName)
                .header("token", expiredToken)
                .when()
                .get("/someone/projects/project-fooId/boards")
                .then()
                .statusCode(401)
                .body("code", equalTo(Constants.SECURITY_IDENTITY_USER_NAME_IS_NOT_CONSISTENT_CODE))
                .body("message", equalTo(Constants.SECURITY_IDENTITY_USER_NAME_IS_NOT_CONSISTENT));
    }

    private String buildToken(String userName, Date date, int minute) throws Exception {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setUserName(userName);


        Date expirationTime = dateService.addMinute(date, minute);
        String expirationTimeStr = dateService.DateToString(expirationTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
        authenticationToken.setExpirationTime(expirationTimeStr);
        String encryptedToken = rsaService.encrypt(authenticationToken.toString());

        return encryptedToken;
    }
}
