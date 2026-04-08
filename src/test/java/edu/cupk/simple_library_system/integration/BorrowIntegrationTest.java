package edu.cupk.simple_library_system.integration;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.entity.BookType;
import edu.cupk.simple_library_system.entity.Borrow;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BorrowIntegrationTest extends BaseIntegrationTest {

    @Test
    void testBorrowBookWorkflow() {
        String username = "borrowtest_" + System.currentTimeMillis();
        String password = "password";

        ResponseEntity<Integer> registerResponse = restTemplate.postForEntity(
                buildUrl("/user/register?username=" + username + "&password=" + password),
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        String token = loginAndGetToken(username, password, (byte) 0);

        ResponseEntity<Map> infoResponse = restTemplate.getForEntity(
                buildUrl("/user/info?token=" + token),
                Map.class
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> userData = (Map<String, Object>) infoResponse.getBody().get("data");
        Integer userId = (Integer) userData.get("userId");

        BookType bookType = new BookType();
        bookType.setBookTypeName("借阅测试类型_" + System.currentTimeMillis());
        bookType.setBookTypeDesc("借阅测试类型描述");

        restTemplate.postForEntity(
                buildUrl("/bookType/addBookType"),
                bookType,
                ApiResponse.class
        );

        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookName("借阅测试图书_" + System.currentTimeMillis());
        bookInfo.setBookAuthor("测试作者");
        bookInfo.setBookPrice(new BigDecimal("50.00"));
        bookInfo.setBookTypeId(1);
        bookInfo.setIsBorrowed((byte) 0);

        restTemplate.postForEntity(
                buildUrl("/bookInfo/addBookInfo"),
                bookInfo,
                ApiResponse.class
        );

        ResponseEntity<ApiResponse> borrowResponse = restTemplate.postForEntity(
                buildUrl("/borrow/borrowBook?userid=" + userId + "&bookid=1"),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, borrowResponse.getStatusCode());
    }

    @Test
    void testBorrowBookFailure() {
        ResponseEntity<ApiResponse> noUserResponse = restTemplate.postForEntity(
                buildUrl("/borrow/borrowBook?userid=99999&bookid=1"),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, noUserResponse.getStatusCode());

        ResponseEntity<ApiResponse> noBookResponse = restTemplate.postForEntity(
                buildUrl("/borrow/borrowBook?userid=1&bookid=99999"),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, noBookResponse.getStatusCode());
    }

    @Test
    void testReturnBookWorkflow() {
        ResponseEntity<ApiResponse> returnResponse = restTemplate.postForEntity(
                buildUrl("/borrow/returnBook?borrowid=1&bookid=1"),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());
    }

    @Test
    void testReturnBookFailure() {
        ResponseEntity<ApiResponse> noBorrowResponse = restTemplate.postForEntity(
                buildUrl("/borrow/returnBook?borrowid=99999&bookid=1"),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, noBorrowResponse.getStatusCode());

        ResponseEntity<ApiResponse> noBookResponse = restTemplate.postForEntity(
                buildUrl("/borrow/returnBook?borrowid=1&bookid=99999"),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, noBookResponse.getStatusCode());
    }

    @Test
    void testQueryBorrowsByPage() {
        ResponseEntity<PageResponse> allResponse = restTemplate.getForEntity(
                buildUrl("/borrow/queryBorrowsByPage?page=1&limit=10"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, allResponse.getStatusCode());
        assertNotNull(allResponse.getBody());
        assertEquals(0, allResponse.getBody().getCode());

        ResponseEntity<PageResponse> userFilterResponse = restTemplate.getForEntity(
                buildUrl("/borrow/queryBorrowsByPage?page=1&limit=10&userid=1"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, userFilterResponse.getStatusCode());

        ResponseEntity<PageResponse> bookFilterResponse = restTemplate.getForEntity(
                buildUrl("/borrow/queryBorrowsByPage?page=1&limit=10&bookid=1"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, bookFilterResponse.getStatusCode());
    }

    @Test
    void testAddBorrow() {
        Borrow borrow = new Borrow();
        borrow.setUserId(1);
        borrow.setBookId(1);

        ResponseEntity<ApiResponse> addResponse = restTemplate.postForEntity(
                buildUrl("/borrow/addBorrow"),
                borrow,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
    }

    @Test
    void testDeleteBorrow() {
        Borrow nonExistentBorrow = new Borrow();
        nonExistentBorrow.setBorrowId(99999);

        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                buildUrl("/borrow/deleteBorrow"),
                HttpMethod.DELETE,
                new HttpEntity<>(nonExistentBorrow),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        Borrow nullIdBorrow = new Borrow();
        nullIdBorrow.setBorrowId(null);

        ResponseEntity<ApiResponse> nullIdResponse = restTemplate.exchange(
                buildUrl("/borrow/deleteBorrow"),
                HttpMethod.DELETE,
                new HttpEntity<>(nullIdBorrow),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, nullIdResponse.getStatusCode());
    }

    @Test
    void testDeleteBorrows() {
        List<Borrow> borrowsToDelete = new ArrayList<>();

        Borrow borrow1 = new Borrow();
        borrow1.setBorrowId(1);
        borrowsToDelete.add(borrow1);

        Borrow borrow2 = new Borrow();
        borrow2.setBorrowId(2);
        borrowsToDelete.add(borrow2);

        Borrow borrow3 = new Borrow();
        borrow3.setBorrowId(99999);
        borrowsToDelete.add(borrow3);

        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                buildUrl("/borrow/deleteBorrows"),
                HttpMethod.DELETE,
                new HttpEntity<>(borrowsToDelete),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
    }

    @Test
    void testUpdateBorrow() {
        Borrow nonExistentBorrow = new Borrow();
        nonExistentBorrow.setBorrowId(99999);
        nonExistentBorrow.setUserId(1);
        nonExistentBorrow.setBookId(1);
        nonExistentBorrow.setBorrowTime(LocalDateTime.now());
        nonExistentBorrow.setReturnTime(LocalDateTime.now());

        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                buildUrl("/borrow/updateBorrow"),
                HttpMethod.PUT,
                new HttpEntity<>(nonExistentBorrow),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        Borrow nullIdBorrow = new Borrow();
        nullIdBorrow.setBorrowId(null);

        ResponseEntity<ApiResponse> nullIdResponse = restTemplate.exchange(
                buildUrl("/borrow/updateBorrow"),
                HttpMethod.PUT,
                new HttpEntity<>(nullIdBorrow),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, nullIdResponse.getStatusCode());
    }

    @Test
    void testReaderBorrowBook() {
        ResponseEntity<ApiResponse> borrowResponse = restTemplate.postForEntity(
                buildUrl("/borrow/reader/borrowBook?userid=1&bookid=1"),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, borrowResponse.getStatusCode());
    }

    @Test
    void testReaderReturnBook() {
        ResponseEntity<ApiResponse> returnResponse = restTemplate.postForEntity(
                buildUrl("/borrow/reader/returnBook?borrowid=1&bookid=1"),
                null,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());
    }

    @Test
    void testGetBorrowCount() {
        ResponseEntity<ApiResponse> countResponse = restTemplate.getForEntity(
                buildUrl("/borrow/getCount"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, countResponse.getStatusCode());
    }
}
