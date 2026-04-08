package edu.cupk.simple_library_system.integration;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.entity.BookType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookTypeIntegrationTest extends BaseIntegrationTest {

    @Test
    void testBookTypeCrudWorkflow() {
        BookType bookType = new BookType();
        bookType.setBookTypeName("集成测试类型_" + System.currentTimeMillis());
        bookType.setBookTypeDesc("集成测试类型描述");

        ResponseEntity<ApiResponse> addResponse = restTemplate.postForEntity(
                buildUrl("/bookType/addBookType"),
                bookType,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, addResponse.getStatusCode());

        ResponseEntity<ApiResponse> countResponse = restTemplate.getForEntity(
                buildUrl("/bookType/getCount"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, countResponse.getStatusCode());

        ResponseEntity<ApiResponse> queryResponse = restTemplate.getForEntity(
                buildUrl("/bookType/queryBookTypes"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, queryResponse.getStatusCode());

        ResponseEntity<PageResponse> pageResponse = restTemplate.getForEntity(
                buildUrl("/bookType/queryBookTypesByPage?page=1&limit=10"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, pageResponse.getStatusCode());
        assertNotNull(pageResponse.getBody());
        assertEquals(0, pageResponse.getBody().getCode());
    }

    @Test
    void testQueryBookTypesByPageWithFilter() {
        BookType bookType = new BookType();
        bookType.setBookTypeName("计算机科学_" + System.currentTimeMillis());
        bookType.setBookTypeDesc("计算机相关书籍");

        restTemplate.postForEntity(
                buildUrl("/bookType/addBookType"),
                bookType,
                ApiResponse.class
        );

        ResponseEntity<PageResponse> allResponse = restTemplate.getForEntity(
                buildUrl("/bookType/queryBookTypesByPage?page=1&limit=10"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, allResponse.getStatusCode());

        ResponseEntity<PageResponse> filterResponse = restTemplate.getForEntity(
                buildUrl("/bookType/queryBookTypesByPage?page=1&limit=10&booktypename=计算机"),
                PageResponse.class
        );

        assertEquals(HttpStatus.OK, filterResponse.getStatusCode());
    }

    @Test
    void testPresetBookTypes() {
        String[] presetTypes = {"计算机科学", "历史", "文学", "科幻", "小说", "外语"};

        for (String typeName : presetTypes) {
            BookType bookType = new BookType();
            bookType.setBookTypeName(typeName + "_" + System.currentTimeMillis());
            bookType.setBookTypeDesc(typeName + "相关书籍");

            ResponseEntity<ApiResponse> addResponse = restTemplate.postForEntity(
                    buildUrl("/bookType/addBookType"),
                    bookType,
                    ApiResponse.class
            );

            assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        }
    }

    @Test
    void testDeleteBookType() {
        BookType bookType = new BookType();
        bookType.setBookTypeName("删除测试类型_" + System.currentTimeMillis());
        bookType.setBookTypeDesc("删除测试类型描述");

        restTemplate.postForEntity(
                buildUrl("/bookType/addBookType"),
                bookType,
                ApiResponse.class
        );

        BookType nonExistentType = new BookType();
        nonExistentType.setBookTypeId(99999);

        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                buildUrl("/bookType/deleteBookType"),
                org.springframework.http.HttpMethod.DELETE,
                new org.springframework.http.HttpEntity<>(nonExistentType),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        BookType nullIdType = new BookType();
        nullIdType.setBookTypeId(null);

        ResponseEntity<ApiResponse> nullIdResponse = restTemplate.exchange(
                buildUrl("/bookType/deleteBookType"),
                org.springframework.http.HttpMethod.DELETE,
                new org.springframework.http.HttpEntity<>(nullIdType),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, nullIdResponse.getStatusCode());
    }

    @Test
    void testDeleteBookTypes() {
        List<BookType> typesToDelete = new ArrayList<>();

        BookType type1 = new BookType();
        type1.setBookTypeId(1);
        typesToDelete.add(type1);

        BookType type2 = new BookType();
        type2.setBookTypeId(2);
        typesToDelete.add(type2);

        BookType type3 = new BookType();
        type3.setBookTypeId(99999);
        typesToDelete.add(type3);

        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                buildUrl("/bookType/deleteBookTypes"),
                org.springframework.http.HttpMethod.DELETE,
                new org.springframework.http.HttpEntity<>(typesToDelete),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
    }

    @Test
    void testUpdateBookType() {
        BookType nonExistentType = new BookType();
        nonExistentType.setBookTypeId(99999);
        nonExistentType.setBookTypeName("不存在的类型");
        nonExistentType.setBookTypeDesc("不存在的类型描述");

        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                buildUrl("/bookType/updateBookType"),
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(nonExistentType),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        BookType nullIdType = new BookType();
        nullIdType.setBookTypeId(null);
        nullIdType.setBookTypeName("空ID类型");

        ResponseEntity<ApiResponse> nullIdResponse = restTemplate.exchange(
                buildUrl("/bookType/updateBookType"),
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(nullIdType),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, nullIdResponse.getStatusCode());
    }

    @Test
    void testReaderQueryBookTypes() {
        ResponseEntity<ApiResponse> queryResponse = restTemplate.getForEntity(
                buildUrl("/bookType/reader/queryBookTypes"),
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, queryResponse.getStatusCode());
    }
}
