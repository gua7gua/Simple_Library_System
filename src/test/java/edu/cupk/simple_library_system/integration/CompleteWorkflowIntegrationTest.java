package edu.cupk.simple_library_system.integration;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.entity.BookType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompleteWorkflowIntegrationTest extends BaseIntegrationTest {

    @Test
    void testCompleteBorrowWorkflow() {
        String username = "workflow_user_" + System.currentTimeMillis();
        String password = "workflow_password";

        ResponseEntity<Integer> registerResponse = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        Object loginRequest = new Object() {
            public String getUsername() { return username; }
            public String getUserpassword() { return password; }
            public Byte getIsAdmin() { return 0; }
        };

        ResponseEntity<ApiResponse> loginResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                loginRequest,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody(), "登录应返回响应体");

        Integer loginStatus = loginResponse.getBody().getStatus();
        String token = null;
        Integer userId = null;

        if (loginStatus != null && loginStatus == 200 && loginResponse.getBody().getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> loginData = (Map<String, String>) loginResponse.getBody().getData();
            token = loginData.get("token");
        }

        BookType bookType = new BookType();
        bookType.setBookTypeName("流程测试类型_" + System.currentTimeMillis());
        bookType.setBookTypeDesc("流程测试类型描述");

        ResponseEntity<ApiResponse> addTypeResponse = restTemplate.postForEntity(
                buildUrl("/bookType/addBookType"),
                bookType,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, addTypeResponse.getStatusCode());

        ResponseEntity<ApiResponse> queryTypesResponse = restTemplate.getForEntity(
                buildUrl("/bookType/queryBookTypes"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, queryTypesResponse.getStatusCode());

        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookName("流程测试图书_" + System.currentTimeMillis());
        bookInfo.setBookAuthor("流程测试作者");
        bookInfo.setBookPrice(new BigDecimal("99.99"));
        bookInfo.setBookTypeId(1);
        bookInfo.setBookDesc("流程测试图书描述");
        bookInfo.setIsBorrowed((byte) 0);

        ResponseEntity<ApiResponse> addBookResponse = restTemplate.postForEntity(
                buildUrl("/bookInfo/addBookInfo"),
                bookInfo,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, addBookResponse.getStatusCode());

        ResponseEntity<PageResponse> queryBooksResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/queryBookInfosByPage?page=1&limit=10"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, queryBooksResponse.getStatusCode());
        assertNotNull(queryBooksResponse.getBody(), "应返回图书列表");

        userId = 1;
        Integer bookId = 1;

        ResponseEntity<ApiResponse> borrowResponse = restTemplate.postForEntity(
                buildUrl("/borrow/borrowBook?userid=" + userId + "&bookid=" + bookId),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, borrowResponse.getStatusCode());

        ResponseEntity<PageResponse> queryBorrowsResponse = restTemplate.getForEntity(
                buildUrl("/borrow/queryBorrowsByPage?page=1&limit=10&userid=" + userId),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, queryBorrowsResponse.getStatusCode());
        assertNotNull(queryBorrowsResponse.getBody(), "应返回借阅记录列表");

        if (token != null) {
            ResponseEntity<ApiResponse> logoutResponse = restTemplate.getForEntity(
                    buildUrl("/user/logout?token=" + token),
                    ApiResponse.class
            );

            assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());
        }
    }

    @Test
    void testAdminWorkflow() {
        Object adminLoginRequest = new Object() {
            public String getUsername() { return "admin"; }
            public String getUserpassword() { return "admin123"; }
            public Byte getIsAdmin() { return 1; }
        };

        ResponseEntity<ApiResponse> loginResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                adminLoginRequest,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        ResponseEntity<ApiResponse> userCountResponse = restTemplate.getForEntity(
                buildUrl("/user/getCount"),
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, userCountResponse.getStatusCode());

        ResponseEntity<ApiResponse> bookCountResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/getCount"),
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, bookCountResponse.getStatusCode());

        ResponseEntity<ApiResponse> borrowCountResponse = restTemplate.getForEntity(
                buildUrl("/borrow/getCount"),
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, borrowCountResponse.getStatusCode());

        ResponseEntity<ApiResponse> usersResponse = restTemplate.getForEntity(
                buildUrl("/user/queryUsers"),
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, usersResponse.getStatusCode());

        ResponseEntity<ApiResponse> typesResponse = restTemplate.getForEntity(
                buildUrl("/bookType/queryBookTypes"),
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, typesResponse.getStatusCode());

        ResponseEntity<ApiResponse> booksResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/queryBookInfos"),
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());
    }

    @Test
    void testReaderWorkflow() {
        String username = "reader_" + System.currentTimeMillis();
        String password = "reader_password";

        ResponseEntity<Integer> registerResponse = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        Object loginRequest = new Object() {
            public String getUsername() { return username; }
            public String getUserpassword() { return password; }
            public Byte getIsAdmin() { return 0; }
        };

        ResponseEntity<ApiResponse> loginResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                loginRequest,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody(), "登录应返回响应体");

        Integer loginStatus = loginResponse.getBody().getStatus();
        String token = null;
        if (loginStatus != null && loginStatus == 200 && loginResponse.getBody().getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> loginData = (Map<String, String>) loginResponse.getBody().getData();
            token = loginData.get("token");
        }

        ResponseEntity<ApiResponse> typesResponse = restTemplate.getForEntity(
                buildUrl("/bookType/reader/queryBookTypes"),
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, typesResponse.getStatusCode());

        ResponseEntity<PageResponse> booksResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/reader/queryBookInfosByPage?page=1&limit=10"),
                PageResponse.class
        );
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode());

        ResponseEntity<ApiResponse> borrowResponse = restTemplate.postForEntity(
                buildUrl("/borrow/reader/borrowBook?userid=1&bookid=1"),
                null,
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, borrowResponse.getStatusCode());

        ResponseEntity<ApiResponse> returnResponse = restTemplate.postForEntity(
                buildUrl("/borrow/reader/returnBook?borrowid=1&bookid=1"),
                null,
                ApiResponse.class
        );
        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());
    }

    @Test
    void testErrorHandlingWorkflow() {
        ResponseEntity<ApiResponse> invalidTokenResponse = restTemplate.getForEntity(
                buildUrl("/user/info?token=invalid_token"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, invalidTokenResponse.getStatusCode());
        assertEquals(420, invalidTokenResponse.getBody().getStatus(), "无效token应返回失败状态");

        String username = "duplicate_test_" + System.currentTimeMillis();
        String password = "password";

        ResponseEntity<Integer> firstRegister = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );
        assertEquals(HttpStatus.OK, firstRegister.getStatusCode());

        ResponseEntity<Integer> secondRegister = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );
        assertEquals(HttpStatus.OK, secondRegister.getStatusCode());

        Object wrongPasswordRequest = new Object() {
            public String getUsername() { return username; }
            public String getUserpassword() { return "wrong_password"; }
            public Byte getIsAdmin() { return 0; }
        };

        ResponseEntity<ApiResponse> wrongPasswordResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                wrongPasswordRequest,
                ApiResponse.class
        );

        assertEquals(420, wrongPasswordResponse.getBody().getStatus(), "错误密码应登录失败");

        ResponseEntity<Object> notFoundResponse = restTemplate.getForEntity(
                buildUrl("/nonexistent/path"),
                Object.class
        );

        assertNotNull(notFoundResponse.getStatusCode());
    }
}
