package edu.cupk.simple_library_system.integration;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/BookManager";
    }

    protected String buildUrl(String path) {
        return baseUrl + path;
    }

    protected String loginAndGetToken(String username, String password, Byte isAdmin) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setUserpassword(password);
        loginRequest.setIsAdmin(isAdmin);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                buildUrl("/user/login"),
                loginRequest,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        if (response.getBody().getStatus() == 200 && response.getBody().getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) response.getBody().getData();
            return data.get("token");
        }
        return null;
    }

    protected HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        return headers;
    }

    protected <T> ResponseEntity<T> get(String url, Class<T> responseType, String token) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(buildUrl(url), HttpMethod.GET, entity, responseType);
    }

    protected <T> ResponseEntity<T> post(String url, Class<T> responseType, String token) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(buildUrl(url), HttpMethod.POST, entity, responseType);
    }

    protected <T> ResponseEntity<T> post(String url, Object requestBody, Class<T> responseType, String token) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(buildUrl(url), HttpMethod.POST, entity, responseType);
    }

    protected <T> ResponseEntity<T> put(String url, Object requestBody, Class<T> responseType, String token) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(buildUrl(url), HttpMethod.PUT, entity, responseType);
    }

    protected <T> ResponseEntity<T> delete(String url, Object requestBody, Class<T> responseType, String token) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(buildUrl(url), HttpMethod.DELETE, entity, responseType);
    }

    protected void assertSuccess(ResponseEntity<?> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    protected void assertStatusCode(ResponseEntity<?> response, HttpStatus expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCode());
    }
}
