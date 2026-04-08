package edu.cupk.simple_library_system.integration;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.AlterPasswordRequest;
import edu.cupk.simple_library_system.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserIntegrationTest extends BaseIntegrationTest {

    @Test
    void testUserRegistration() {
        String username = "testuser_" + System.currentTimeMillis();
        String password = "testpassword";

        ResponseEntity<Integer> registerResponse = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        assertEquals(1, registerResponse.getBody());

        ResponseEntity<Integer> duplicateResponse = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, duplicateResponse.getStatusCode());
        assertEquals(0, duplicateResponse.getBody());
    }

    @Test
    void testUserLogin() {
        String username = "logintest_" + System.currentTimeMillis();
        String password = "testpassword";

        restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );

        ResponseEntity<ApiResponse> loginResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                createLoginRequest(username, password, (byte) 0),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertNotNull(loginResponse.getBody().getStatus());

        ResponseEntity<ApiResponse> failResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                createLoginRequest(username, "wrongpassword", (byte) 0),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, failResponse.getStatusCode());
        assertEquals(420, failResponse.getBody().getStatus());
    }

    @Test
    void testGetUserInfo() {
        String username = "infotest_" + System.currentTimeMillis();
        String password = "testpassword";

        restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );

        String token = loginAndGetToken(username, password, (byte) 0);

        if (token != null) {
            ResponseEntity<ApiResponse> infoResponse = restTemplate.getForEntity(
                    buildUrl("/user/info?token=" + token),
                    ApiResponse.class
            );

            assertEquals(HttpStatus.OK, infoResponse.getStatusCode());
            assertNotNull(infoResponse.getBody());
        }
    }

    @Test
    void testAlterPassword() {
        String username = "pwdtest_" + System.currentTimeMillis();
        String oldPassword = "oldpassword";
        String newPassword = "newpassword";

        restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + oldPassword),
                null,
                Integer.class
        );

        String token = loginAndGetToken(username, oldPassword, (byte) 0);

        ResponseEntity<ApiResponse> infoResponse = restTemplate.getForEntity(
                buildUrl("/user/info?token=" + token),
                ApiResponse.class
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> userData = (Map<String, Object>) infoResponse.getBody().getData();
        Integer userId = (Integer) userData.get("userId");

        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(userId);
        request.setUserName(username);
        request.setIsAdmin((byte) 0);
        request.setOldPassword(oldPassword);
        request.setNewPassword(newPassword);

        ResponseEntity<ApiResponse> alterResponse = restTemplate.postForEntity(
                buildUrl("/user/alterPassword"),
                request,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, alterResponse.getStatusCode());
        assertNotNull(alterResponse.getBody().getStatus());

        String newToken = loginAndGetToken(username, newPassword, (byte) 0);

        ResponseEntity<ApiResponse> oldLoginResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                createLoginRequest(username, oldPassword, (byte) 0),
                ApiResponse.class
        );

        assertEquals(420, oldLoginResponse.getBody().getStatus());
    }

    @Test
    void testQueryUsersByPage() {
        for (int i = 0; i < 5; i++) {
            restTemplate.postForEntity(
                    buildUrl("/user/register?username=pagetest_" + i + "_" + System.currentTimeMillis() + "&password=password"),
                    null,
                    Integer.class
            );
        }

        ResponseEntity<PageResponse> pageResponse = restTemplate.getForEntity(
                buildUrl("/user/queryUsersByPage?page=1&limit=3"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, pageResponse.getStatusCode());
        assertNotNull(pageResponse.getBody());
        assertEquals(0, pageResponse.getBody().getCode());

        ResponseEntity<PageResponse> filterResponse = restTemplate.getForEntity(
                buildUrl("/user/queryUsersByPage?page=1&limit=10&username=pagetest"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, filterResponse.getStatusCode());
        assertNotNull(filterResponse.getBody());
    }

    @Test
    void testUserCrudWorkflow() {
        User newUser = new User();
        newUser.setUserName("crudtest_" + System.currentTimeMillis());
        newUser.setUserPassword("password");
        newUser.setIsAdmin((byte) 0);

        ResponseEntity<ApiResponse> addResponse = restTemplate.postForEntity(
                buildUrl("/user/addUser"),
                newUser,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        assertNotNull(addResponse.getBody());

        ResponseEntity<ApiResponse> queryResponse = restTemplate.getForEntity(
                buildUrl("/user/queryUsers"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, queryResponse.getStatusCode());
    }

    private Object createLoginRequest(String username, String password, Byte isAdmin) {
        return new Object() {
            public String getUsername() { return username; }
            public String getUserpassword() { return password; }
            public Byte getIsAdmin() { return isAdmin; }
        };
    }
}
