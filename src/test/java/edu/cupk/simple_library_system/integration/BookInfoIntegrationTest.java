package edu.cupk.simple_library_system.integration;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.entity.BookType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookInfoIntegrationTest extends BaseIntegrationTest {

    @Test
    void testBookInfoCrudWorkflow() {
        BookType bookType = new BookType();
        bookType.setBookTypeName("测试类型_" + System.currentTimeMillis());
        bookType.setBookTypeDesc("测试类型描述");

        ResponseEntity<ApiResponse> typeResponse = restTemplate.postForEntity(
                buildUrl("/bookType/addBookType"),
                bookType,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, typeResponse.getStatusCode());

        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookName("测试图书_" + System.currentTimeMillis());
        bookInfo.setBookAuthor("测试作者");
        bookInfo.setBookPrice(new BigDecimal("50.00"));
        bookInfo.setBookTypeId(1);
        bookInfo.setBookDesc("测试图书描述");
        bookInfo.setIsBorrowed((byte) 0);

        ResponseEntity<ApiResponse> addResponse = restTemplate.postForEntity(
                buildUrl("/bookInfo/addBookInfo"),
                bookInfo,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        assertNotNull(addResponse.getBody());

        ResponseEntity<ApiResponse> countResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/getCount"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, countResponse.getStatusCode());

        ResponseEntity<ApiResponse> queryResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/queryBookInfos"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, queryResponse.getStatusCode());

        ResponseEntity<PageResponse> pageResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/queryBookInfosByPage?page=1&limit=10"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, pageResponse.getStatusCode());
        assertNotNull(pageResponse.getBody());
        assertEquals(0, pageResponse.getBody().getCode());
    }

    @Test
    void testQueryBookInfosByPageWithFilters() {
        ResponseEntity<PageResponse> allResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/queryBookInfosByPage?page=1&limit=10"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, allResponse.getStatusCode());

        ResponseEntity<PageResponse> nameResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookname=Java"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, nameResponse.getStatusCode());

        ResponseEntity<PageResponse> authorResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookauthor=Bruce"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, authorResponse.getStatusCode());

        ResponseEntity<PageResponse> typeResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/queryBookInfosByPage?page=1&limit=10&booktypeid=1"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, typeResponse.getStatusCode());
    }

    @Test
    void testDeleteBookInfoWithBorrowStatus() {
        BookType bookType = new BookType();
        bookType.setBookTypeName("删除测试类型_" + System.currentTimeMillis());
        bookType.setBookTypeDesc("删除测试类型描述");

        restTemplate.postForEntity(
                buildUrl("/bookType/addBookType"),
                bookType,
                ApiResponse.class
        );

        BookInfo unborrowedBook = new BookInfo();
        unborrowedBook.setBookName("未借阅图书_" + System.currentTimeMillis());
        unborrowedBook.setBookAuthor("测试作者");
        unborrowedBook.setBookPrice(new BigDecimal("50.00"));
        unborrowedBook.setBookTypeId(1);
        unborrowedBook.setIsBorrowed((byte) 0);

        restTemplate.postForEntity(
                buildUrl("/bookInfo/addBookInfo"),
                unborrowedBook,
                ApiResponse.class
        );

        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(1);

        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                buildUrl("/bookInfo/deleteBookInfo"),
                HttpMethod.DELETE,
                new HttpEntity<>(bookToDelete),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

    @Test
    void testDeleteBookInfos() {
        List<BookInfo> booksToDelete = new ArrayList<>();

        BookInfo book1 = new BookInfo();
        book1.setBookId(1);
        booksToDelete.add(book1);

        BookInfo book2 = new BookInfo();
        book2.setBookId(2);
        booksToDelete.add(book2);

        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                buildUrl("/bookInfo/deleteBookInfos"),
                HttpMethod.DELETE,
                new HttpEntity<>(booksToDelete),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
    }

    @Test
    void testUpdateBookInfo() {
        BookInfo nonExistentBook = new BookInfo();
        nonExistentBook.setBookId(99999);
        nonExistentBook.setBookName("不存在的图书");
        nonExistentBook.setBookAuthor("测试作者");
        nonExistentBook.setBookPrice(new BigDecimal("100.00"));
        nonExistentBook.setBookTypeId(1);

        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                buildUrl("/bookInfo/updateBookInfo"),
                HttpMethod.PUT,
                new HttpEntity<>(nonExistentBook),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    @Test
    void testReaderQueryBookInfos() {
        ResponseEntity<ApiResponse> queryResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/reader/queryBookInfos"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, queryResponse.getStatusCode());

        ResponseEntity<PageResponse> pageResponse = restTemplate.getForEntity(
                buildUrl("/bookInfo/reader/queryBookInfosByPage?page=1&limit=10"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, pageResponse.getStatusCode());
        assertNotNull(pageResponse.getBody());
    }
}
