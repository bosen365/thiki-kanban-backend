package org.thiki.kanban.registration;

import com.alibaba.fastjson.JSONObject;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.thiki.kanban.TestBase;
import org.thiki.kanban.foundation.annotations.Domain;
import org.thiki.kanban.foundation.annotations.Scenario;
import org.thiki.kanban.foundation.application.DomainOrder;
import org.thiki.kanban.foundation.security.identification.rsa.RSAService;
import org.thiki.kanban.user.UsersCodes;
import org.thiki.kanban.user.registration.RegistrationService;

import javax.annotation.Resource;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertEquals;

/**
 * Created by xubt on 6/16/16.
 */
@Domain(order = DomainOrder.REGISTRATION, name = "注册")
@RunWith(SpringJUnit4ClassRunner.class)
public class RegistrationControllerTest extends TestBase {
    @Resource
    private RegistrationService registrationService;

    @Resource
    private RSAService rsaService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ReflectionTestUtils.setField(registrationService, "sequenceNumber", sequenceNumber);

    }

    @Scenario("注册成功>根据服务端提供的公钥对密码进行加密,服务端拿到加密的密码后,首先用私钥解密,再通过MD5算法加盐加密")
    @Test
    public void registerNewUser_shouldReturn201WhenRegisterSuccessfully() throws Exception {
        String publicKey = rsaService.loadKey(publicKeyFilePath);
        String password = "foo";
        String rsaPassword = rsaService.encrypt(publicKey, password);
        String expectedMd5Password = "148412d9df986f739038ad22c77459f2";

        JSONObject body = new JSONObject();
        body.put("name", "someone");
        body.put("email", "someone@gmail.com");
        body.put("password", rsaPassword);
        given().body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/registration")
                .then()
                .statusCode(201)
                .body("name", equalTo("someone"))
                .body("id", equalTo("fooId"))
                .body("email", equalTo("someone@gmail.com"))
                .body("_links.login.href", endsWith("/login"));

        assertEquals(1, jdbcTemplate.queryForList("SELECT * FROM kb_user_registration").size());
        assertEquals(expectedMd5Password, jdbcTemplate.queryForObject("SELECT password FROM kb_user_registration", String.class));
    }

    @Scenario("不允许注册>如果用户名已经存在,则不允许注册")
    @Test
    public void registerNewUser_shouldRejectWithConflictWhenUserNameExists() {
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,name,password) " +
                "VALUES ('fooUserId','someone@gmail.com','someone','password')");

        given().body("{\"name\":\"someone\",\"email\":\"someone@gmail.com\"," +
                "\"password\":\"fee\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/registration")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", equalTo(UsersCodes.USERNAME_IS_ALREADY_EXISTS.code()))
                .body("message", equalTo(UsersCodes.USERNAME_IS_ALREADY_EXISTS.message()));
    }

    @Scenario("不允许注册>如果邮箱已经存在,则不允许注册")
    @Test
    public void registerNewUser_shouldRejectWithConflictWhenUserEmailExists() {
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,name,password) " +
                "VALUES ('fooUserId','someone@gmail.com','someone','password')");

        given().body("{\"name\":\"someoneElse\",\"email\":\"someone@gmail.com\"," +
                "\"password\":\"fee\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/registration")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", equalTo(UsersCodes.EMAIL_IS_ALREADY_EXISTS.code()))
                .body("message", equalTo(UsersCodes.EMAIL_IS_ALREADY_EXISTS.message()));
    }

    @Scenario("不允许注册>用户名和邮箱在系统中都不存在,但是密码未通过公钥加密,则不允许注册")
    @Test
    public void registerNewUser_shouldFailIfPasswordIsNotEncryptedWithPublicKey() throws Exception {
        String password = "foo";
        JSONObject body = new JSONObject();
        body.put("name", "someone");
        body.put("email", "someone@gmail.com");
        body.put("password", password);
        given().body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/registration")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("message", equalTo("通过私钥解密失败,请确保数据已经通过公钥加密。"));

        assertEquals(0, jdbcTemplate.queryForList("SELECT * FROM kb_user_registration").size());
    }
}
