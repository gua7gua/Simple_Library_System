package edu.cupk.simple_library_system.integration;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.LoginRequest;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.entity.BookType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompleteWorkflowIntegrationTest extends BaseIntegrationTest {

    @Test
    void testCompleteBorrowWorkflow() {
        String adminToken = loginAndGetToken("admin", "admin", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        String username = "workflow_user_" + System.currentTimeMillis();
        String password = "workflow_password";

        @SuppressWarnings("unchecked")
        ResponseEntity<Map> registerResponse = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Map.class
        );

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode(), "注册请求HTTP状态码必须是200");
        assertNotNull(registerResponse.getBody(), "注册响应体不能为null");
        Integer registerStatus = (Integer) registerResponse.getBody().get("status");
        assertNotNull(registerStatus, "注册响应必须包含status字段");
        assertEquals(200, registerStatus, "注册业务状态码必须是200");

        String userToken = loginAndGetToken(username, password, (byte) 0);
        assertNotNull(userToken, "用户登录必须成功获取Token");

        ResponseEntity<Map> userInfoResponse = get("/user/info?token=" + userToken, Map.class, userToken);
        assertEquals(HttpStatus.OK, userInfoResponse.getStatusCode(), "获取用户信息请求HTTP状态码必须是200");
        assertNotNull(userInfoResponse.getBody(), "获取用户信息响应体不能为null");

        @SuppressWarnings("unchecked")
        Map<String, Object> userData = userInfoResponse.getBody();
        Integer userId = (Integer) userData.get("userid");
        assertNotNull(userId, "用户信息必须包含userid");

        String typeName = "流程测试类型_" + System.currentTimeMillis();
        BookType bookType = new BookType();
        bookType.setBookTypeName(typeName);
        bookType.setBookTypeDesc("流程测试类型描述");

        ResponseEntity<Integer> addTypeResponse = post("/bookType/addBookType", bookType, Integer.class, adminToken);

        assertEquals(HttpStatus.OK, addTypeResponse.getStatusCode(), "添加图书类型请求HTTP状态码必须是200");
        assertNotNull(addTypeResponse.getBody(), "添加图书类型响应体不能为null");
        assertEquals(1, addTypeResponse.getBody(), "添加图书类型必须返回1");

        ResponseEntity<List> queryTypesResponse = get("/bookType/queryBookTypes", List.class, adminToken);

        assertEquals(HttpStatus.OK, queryTypesResponse.getStatusCode(), "查询图书类型请求HTTP状态码必须是200");
        assertNotNull(queryTypesResponse.getBody(), "查询图书类型响应体不能为null");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> types = queryTypesResponse.getBody();
        Integer bookTypeId = types.stream()
                .filter(t -> typeName.equals(t.get("booktypename")))
                .findFirst()
                .map(t -> (Integer) t.get("booktypeid"))
                .orElse(null);

        assertNotNull(bookTypeId, "必须找到刚添加的图书类型ID");

        String bookName = "流程测试图书_" + System.currentTimeMillis();
        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookName(bookName);
        bookInfo.setBookAuthor("流程测试作者");
        bookInfo.setBookPrice(new BigDecimal("99.99"));
        bookInfo.setBookTypeId(bookTypeId);
        bookInfo.setBookDesc("流程测试图书描述");
        bookInfo.setIsBorrowed((byte) 0);

        ResponseEntity<Integer> addBookResponse = post("/bookInfo/addBookInfo", bookInfo, Integer.class, adminToken);

        assertEquals(HttpStatus.OK, addBookResponse.getStatusCode(), "添加图书请求HTTP状态码必须是200");
        assertNotNull(addBookResponse.getBody(), "添加图书响应体不能为null");
        assertEquals(1, addBookResponse.getBody(), "添加图书必须返回1");

        ResponseEntity<PageResponse> queryBooksResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10", PageResponse.class, adminToken);

        assertEquals(HttpStatus.OK, queryBooksResponse.getStatusCode(), "查询图书请求HTTP状态码必须是200");
        assertNotNull(queryBooksResponse.getBody(), "查询图书响应体不能为null");
        assertEquals(0, queryBooksResponse.getBody().getCode(), "查询图书成功业务状态码必须是0");
        assertNotNull(queryBooksResponse.getBody().getData(), "查询图书数据不能为null");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> books = (List<Map<String, Object>>) queryBooksResponse.getBody().getData();
        Integer bookId = books.stream()
                .filter(b -> bookName.equals(b.get("bookname")))
                .findFirst()
                .map(b -> (Integer) b.get("bookid"))
                .orElse(null);

        assertNotNull(bookId, "必须找到刚添加的图书ID");

        ResponseEntity<Integer> borrowResponse = post("/borrow/borrowBook?userid=" + userId + "&bookid=" + bookId, null, Integer.class, adminToken);

        assertEquals(HttpStatus.OK, borrowResponse.getStatusCode(), "借阅图书请求HTTP状态码必须是200");
        assertNotNull(borrowResponse.getBody(), "借阅图书响应体不能为null");
        assertEquals(1, borrowResponse.getBody(), "借阅图书必须返回1");

        ResponseEntity<PageResponse> queryBorrowsResponse = get("/borrow/queryBorrowsByPage?page=1&limit=10&userid=" + userId, PageResponse.class, adminToken);

        assertEquals(HttpStatus.OK, queryBorrowsResponse.getStatusCode(), "查询借阅记录请求HTTP状态码必须是200");
        assertNotNull(queryBorrowsResponse.getBody(), "查询借阅记录响应体不能为null");
        assertEquals(0, queryBorrowsResponse.getBody().getCode(), "查询借阅记录成功业务状态码必须是0");
        assertNotNull(queryBorrowsResponse.getBody().getData(), "查询借阅记录数据不能为null");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> borrows = (List<Map<String, Object>>) queryBorrowsResponse.getBody().getData();
        assertTrue(borrows.size() > 0, "借阅记录列表必须不为空");

        Integer borrowId = borrows.stream()
                .filter(b -> bookId.equals(b.get("bookid")) && userId.equals(b.get("userid")))
                .findFirst()
                .map(b -> (Integer) b.get("borrowid"))
                .orElse(null);

        assertNotNull(borrowId, "必须找到刚创建的借阅记录ID");

        ResponseEntity<Map> logoutResponse = get("/user/logout?token=" + userToken, Map.class, userToken);

        assertEquals(HttpStatus.OK, logoutResponse.getStatusCode(), "登出请求HTTP状态码必须是200");
        assertNotNull(logoutResponse.getBody(), "登出响应体不能为null");
    }

    @Test
    void testAdminWorkflow() {
        String adminToken = loginAndGetToken("admin", "admin", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        ResponseEntity<Long> userCountResponse = get("/user/getCount", Long.class, adminToken);
        assertEquals(HttpStatus.OK, userCountResponse.getStatusCode(), "查询用户数量请求HTTP状态码必须是200");
        assertNotNull(userCountResponse.getBody(), "查询用户数量响应体不能为null");

        ResponseEntity<Long> bookCountResponse = get("/bookInfo/getCount", Long.class, adminToken);
        assertEquals(HttpStatus.OK, bookCountResponse.getStatusCode(), "查询图书数量请求HTTP状态码必须是200");
        assertNotNull(bookCountResponse.getBody(), "查询图书数量响应体不能为null");

        ResponseEntity<Long> borrowCountResponse = get("/borrow/getCount", Long.class, adminToken);
        assertEquals(HttpStatus.OK, borrowCountResponse.getStatusCode(), "查询借阅数量请求HTTP状态码必须是200");
        assertNotNull(borrowCountResponse.getBody(), "查询借阅数量响应体不能为null");

        ResponseEntity<List> usersResponse = get("/user/queryUsers", List.class, adminToken);
        assertEquals(HttpStatus.OK, usersResponse.getStatusCode(), "查询用户列表请求HTTP状态码必须是200");
        assertNotNull(usersResponse.getBody(), "查询用户列表响应体不能为null");

        ResponseEntity<List> typesResponse = get("/bookType/queryBookTypes", List.class, adminToken);
        assertEquals(HttpStatus.OK, typesResponse.getStatusCode(), "查询图书类型请求HTTP状态码必须是200");
        assertNotNull(typesResponse.getBody(), "查询图书类型响应体不能为null");

        ResponseEntity<PageResponse> booksResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10", PageResponse.class, adminToken);
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode(), "查询图书请求HTTP状态码必须是200");
        assertNotNull(booksResponse.getBody(), "查询图书响应体不能为null");
        assertEquals(0, booksResponse.getBody().getCode(), "查询图书成功业务状态码必须是0");
    }

    @Test
    void testReaderWorkflow() {
        String adminToken = loginAndGetToken("admin", "admin", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        String username = "reader_" + System.currentTimeMillis();
        String password = "reader_password";

        ResponseEntity<Integer> registerResponse = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode(), "读者注册请求HTTP状态码必须是200");
        assertNotNull(registerResponse.getBody(), "读者注册响应体不能为null");
        assertEquals(1, registerResponse.getBody(), "读者注册必须返回1");

        String userToken = loginAndGetToken(username, password, (byte) 0);
        assertNotNull(userToken, "用户登录必须成功获取Token");

        ResponseEntity<Map> userInfoResponse = get("/user/info?token=" + userToken, Map.class, userToken);
        assertEquals(HttpStatus.OK, userInfoResponse.getStatusCode(), "读者获取用户信息请求HTTP状态码必须是200");
        assertNotNull(userInfoResponse.getBody(), "读者获取用户信息响应体不能为null");

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = userInfoResponse.getBody();
        Integer status = (Integer) responseBody.get("status");
        assertNotNull(status, "读者获取用户信息响应必须包含status字段");
        assertEquals(200, status, "读者获取用户信息业务状态码必须是200");

        @SuppressWarnings("unchecked")
        Map<String, Object> userData = (Map<String, Object>) responseBody.get("data");
        assertNotNull(userData, "读者用户信息data不能为null");
        Integer userId = (Integer) userData.get("userId");
        assertNotNull(userId, "读者用户信息必须包含userId");

        ResponseEntity<List> typesResponse = get("/bookType/reader/queryBookTypes", List.class, userToken);
        assertEquals(HttpStatus.OK, typesResponse.getStatusCode(), "读者查询图书类型请求HTTP状态码必须是200");
        assertNotNull(typesResponse.getBody(), "读者查询图书类型响应体不能为null");

        ResponseEntity<PageResponse> booksResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/reader/queryBookInfosByPage?page=1&limit=10"),
                PageResponse.class
        );
        assertEquals(HttpStatus.OK, booksResponse.getStatusCode(), "读者查询图书请求HTTP状态码必须是200");
        assertNotNull(booksResponse.getBody(), "读者查询图书响应体不能为null");
        assertEquals(0, booksResponse.getBody().getCode(), "读者查询图书成功业务状态码必须是0");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> books = (List<Map<String, Object>>) booksResponse.getBody().getData();
        assertTrue(books.size() > 0, "图书列表必须不为空，否则无法进行借阅测试");

        Integer bookId = (Integer) books.get(0).get("bookid");
        assertNotNull(bookId, "第一本图书ID不能为null");

        ResponseEntity<Integer> borrowResponse = restTemplate.postForEntity(
                buildUrl("/borrow/reader/borrowBook?userid=" + userId + "&bookid=" + bookId),
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, borrowResponse.getStatusCode(), "读者借阅图书请求HTTP状态码必须是200");
        assertNotNull(borrowResponse.getBody(), "读者借阅图书响应体不能为null");

        if (borrowResponse.getBody() == 1) {
            ResponseEntity<PageResponse> queryBorrowsResponse = get("/borrow/queryBorrowsByPage?page=1&limit=10&userid=" + userId, PageResponse.class, adminToken);

            assertEquals(HttpStatus.OK, queryBorrowsResponse.getStatusCode(), "查询借阅记录请求HTTP状态码必须是200");
            assertNotNull(queryBorrowsResponse.getBody(), "查询借阅记录响应体不能为null");
            assertEquals(0, queryBorrowsResponse.getBody().getCode(), "查询借阅记录成功业务状态码必须是0");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> borrows = (List<Map<String, Object>>) queryBorrowsResponse.getBody().getData();
            Integer borrowId = borrows.stream()
                    .filter(b -> bookId.equals(b.get("bookid")) && userId.equals(b.get("userid")))
                    .findFirst()
                    .map(b -> (Integer) b.get("borrowid"))
                    .orElse(null);

            assertNotNull(borrowId, "必须找到刚创建的借阅记录ID");

            ResponseEntity<Integer> returnResponse = restTemplate.postForEntity(
                    buildUrl("/borrow/reader/returnBook?borrowid=" + borrowId + "&bookid=" + bookId),
                    null,
                    Integer.class
            );

            assertEquals(HttpStatus.OK, returnResponse.getStatusCode(), "读者归还图书请求HTTP状态码必须是200");
            assertNotNull(returnResponse.getBody(), "读者归还图书响应体不能为null");
            assertEquals(1, returnResponse.getBody(), "读者归还图书必须返回1");
        }
    }

    @Test
    void testErrorHandlingWorkflow() {
        ResponseEntity<Map> invalidTokenResponse = restTemplate.getForEntity(
                buildUrl("/user/info?token=invalid_token"),
                Map.class
        );

        assertEquals(HttpStatus.OK, invalidTokenResponse.getStatusCode(), "无效token请求HTTP状态码必须是200");
        assertNotNull(invalidTokenResponse.getBody(), "无效token响应体不能为null");

        String username = "duplicate_test_" + System.currentTimeMillis();
        String password = "password";

        @SuppressWarnings("unchecked")
        ResponseEntity<Map> firstRegister = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Map.class
        );
        assertEquals(HttpStatus.OK, firstRegister.getStatusCode(), "首次注册请求HTTP状态码必须是200");
        assertNotNull(firstRegister.getBody(), "首次注册响应体不能为null");
        Integer firstRegisterStatus = (Integer) firstRegister.getBody().get("status");
        assertNotNull(firstRegisterStatus, "首次注册响应必须包含status字段");
        assertEquals(200, firstRegisterStatus, "首次注册业务状态码必须是200");

        @SuppressWarnings("unchecked")
        ResponseEntity<Map> secondRegister = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Map.class
        );
        assertEquals(HttpStatus.OK, secondRegister.getStatusCode(), "重复注册请求HTTP状态码必须是200");
        assertNotNull(secondRegister.getBody(), "重复注册响应体不能为null");
        Integer secondRegisterStatus = (Integer) secondRegister.getBody().get("status");
        assertNotNull(secondRegisterStatus, "重复注册响应必须包含status字段");
        assertEquals(200, secondRegisterStatus, "重复注册业务状态码必须是200");

        LoginRequest wrongPasswordRequest = new LoginRequest();
        wrongPasswordRequest.setUsername(username);
        wrongPasswordRequest.setUserpassword("wrongpassword");
        wrongPasswordRequest.setIsAdmin((byte) 0);

        ResponseEntity<Map> wrongPasswordResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                wrongPasswordRequest,
                Map.class
        );
        assertEquals(HttpStatus.OK, wrongPasswordResponse.getStatusCode(), "错误密码登录请求HTTP状态码必须是200");
        assertNotNull(wrongPasswordResponse.getBody(), "错误密码登录响应体不能为null");

        LoginRequest wrongRoleRequest = new LoginRequest();
        wrongRoleRequest.setUsername(username);
        wrongRoleRequest.setUserpassword(password);
        wrongRoleRequest.setIsAdmin((byte) 1);

        ResponseEntity<Map> wrongRoleResponse = restTemplate.postForEntity(
                buildUrl("/user/login"),
                wrongRoleRequest,
                Map.class
        );
        assertEquals(HttpStatus.OK, wrongRoleResponse.getStatusCode(), "错误角色登录请求HTTP状态码必须是200");
        assertNotNull(wrongRoleResponse.getBody(), "错误角色登录响应体不能为null");
    }

    @Test
    void testNonExistentResources() {
        String adminToken = loginAndGetToken("admin", "admin", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        ResponseEntity<Integer> borrowNonExistentUser = post("/borrow/borrowBook?userid=99999&bookid=1", null, Integer.class, adminToken);
        assertEquals(HttpStatus.OK, borrowNonExistentUser.getStatusCode(), "借阅不存在用户请求HTTP状态码必须是200");
        assertEquals(0, borrowNonExistentUser.getBody(), "借阅不存在用户必须返回0");

        ResponseEntity<Integer> borrowNonExistentBook = post("/borrow/borrowBook?userid=1&bookid=99999", null, Integer.class, adminToken);
        assertEquals(HttpStatus.OK, borrowNonExistentBook.getStatusCode(), "借阅不存在图书请求HTTP状态码必须是200");
        assertEquals(0, borrowNonExistentBook.getBody(), "借阅不存在图书必须返回0");

        ResponseEntity<Integer> returnNonExistentBorrow = post("/borrow/returnBook?borrowid=99999&bookid=1", null, Integer.class, adminToken);
        assertEquals(HttpStatus.OK, returnNonExistentBorrow.getStatusCode(), "归还不存在借阅请求HTTP状态码必须是200");
        assertEquals(0, returnNonExistentBorrow.getBody(), "归还不存在借阅必须返回0");
    }
}
