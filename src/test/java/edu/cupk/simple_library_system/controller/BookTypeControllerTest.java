package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.entity.BookType;
import edu.cupk.simple_library_system.repository.BookTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookTypeControllerTest {

    @Autowired
    private BookTypeController bookTypeController;

    @MockitoBean
    private BookTypeRepository bookTypeRepository;

    private BookType testBookType;
    private List<BookType> presetBookTypes;

    @BeforeEach
    void setUp() {
        testBookType = new BookType();
        testBookType.setBookTypeId(1);
        testBookType.setBookTypeName("计算机科学");
        testBookType.setBookTypeDesc("计算机相关书籍");

        presetBookTypes = new ArrayList<>();
        presetBookTypes.add(createBookType(1, "计算机科学", "计算机相关书籍"));
        presetBookTypes.add(createBookType(2, "历史", "历史相关书籍"));
        presetBookTypes.add(createBookType(3, "文学", "文学相关书籍"));
        presetBookTypes.add(createBookType(4, "科幻", "科幻相关书籍"));
        presetBookTypes.add(createBookType(5, "小说", "小说相关书籍"));
        presetBookTypes.add(createBookType(6, "外语", "外语学习相关书籍"));
    }

    private BookType createBookType(Integer id, String name, String desc) {
        BookType type = new BookType();
        type.setBookTypeId(id);
        type.setBookTypeName(name);
        type.setBookTypeDesc(desc);
        return type;
    }

    @Test
    void testGetCount() {
        when(bookTypeRepository.count()).thenReturn(6L);

        long count = bookTypeController.getCount();

        assertEquals(6L, count);
    }

    @Test
    void testQueryBookTypes() {
        when(bookTypeRepository.findAll()).thenReturn(presetBookTypes);

        List<BookType> result = bookTypeController.queryBookTypes();

        assertEquals(6, result.size());
        assertEquals("计算机科学", result.get(0).getBookTypeName());
        assertEquals("历史", result.get(1).getBookTypeName());
        assertEquals("文学", result.get(2).getBookTypeName());
        assertEquals("科幻", result.get(3).getBookTypeName());
        assertEquals("小说", result.get(4).getBookTypeName());
        assertEquals("外语", result.get(5).getBookTypeName());
    }

    @Test
    void testReaderQueryBookTypes() {
        when(bookTypeRepository.findAll()).thenReturn(presetBookTypes);

        List<BookType> result = bookTypeController.readerQueryBookTypes();

        assertEquals(6, result.size());
        assertEquals("计算机科学", result.get(0).getBookTypeName());
    }

    @Test
    void testQueryBookTypesByPage() {
        Page<BookType> page = new PageImpl<>(presetBookTypes);

        when(bookTypeRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, null);

        assertEquals(6, result.getCount());
        assertEquals(6, result.getData().size());
    }

    @Test
    void testQueryBookTypesByPage_WithBookTypeName() {
        List<BookType> filteredTypes = new ArrayList<>();
        filteredTypes.add(presetBookTypes.get(0));
        Page<BookType> page = new PageImpl<>(filteredTypes);

        when(bookTypeRepository.findByBookTypeNameContaining("计算机", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, "计算机");

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("计算机科学", result.getData().get(0).getBookTypeName());
    }

    @Test
    void testQueryBookTypesByPage_WithFuzzySearch() {
        List<BookType> filteredTypes = new ArrayList<>();
        filteredTypes.add(presetBookTypes.get(3));
        filteredTypes.add(presetBookTypes.get(4));
        Page<BookType> page = new PageImpl<>(filteredTypes);

        when(bookTypeRepository.findByBookTypeNameContaining("说", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, "说");

        assertEquals(2, result.getCount());
    }

    @Test
    void testAddBookType_Success() {
        BookType newType = new BookType();
        newType.setBookTypeName("艺术");
        newType.setBookTypeDesc("艺术相关书籍");

        when(bookTypeRepository.save(any(BookType.class))).thenReturn(newType);

        Integer result = bookTypeController.addBookType(newType);

        assertEquals(1, result);
        verify(bookTypeRepository, times(1)).save(any(BookType.class));
    }

    @Test
    void testAddBookType_PresetType() {
        BookType newType = new BookType();
        newType.setBookTypeName("哲学");
        newType.setBookTypeDesc("哲学相关书籍");

        when(bookTypeRepository.save(any(BookType.class))).thenReturn(newType);

        Integer result = bookTypeController.addBookType(newType);

        assertEquals(1, result);
    }

    @Test
    void testDeleteBookType_Success() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(1);

        when(bookTypeRepository.existsById(1)).thenReturn(true);
        doNothing().when(bookTypeRepository).deleteById(1);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(1, result);
        verify(bookTypeRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteBookType_NullId() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(null);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(0, result);
    }

    @Test
    void testDeleteBookType_NotFound() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(999);

        when(bookTypeRepository.existsById(999)).thenReturn(false);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(0, result);
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
        type3.setBookTypeId(999);
        typesToDelete.add(type3);

        when(bookTypeRepository.existsById(1)).thenReturn(true);
        when(bookTypeRepository.existsById(2)).thenReturn(true);
        when(bookTypeRepository.existsById(999)).thenReturn(false);
        doNothing().when(bookTypeRepository).deleteById(anyInt());

        Integer result = bookTypeController.deleteBookTypes(typesToDelete);

        assertEquals(2, result);
        verify(bookTypeRepository, times(1)).deleteById(1);
        verify(bookTypeRepository, times(1)).deleteById(2);
        verify(bookTypeRepository, never()).deleteById(999);
    }

    @Test
    void testDeleteBookTypes_WithNullId() {
        List<BookType> typesToDelete = new ArrayList<>();

        BookType type1 = new BookType();
        type1.setBookTypeId(null);
        typesToDelete.add(type1);

        BookType type2 = new BookType();
        type2.setBookTypeId(1);
        typesToDelete.add(type2);

        when(bookTypeRepository.existsById(1)).thenReturn(true);
        doNothing().when(bookTypeRepository).deleteById(1);

        Integer result = bookTypeController.deleteBookTypes(typesToDelete);

        assertEquals(1, result);
    }

    @Test
    void testUpdateBookType_Success() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(1);
        typeToUpdate.setBookTypeName("计算机科学与技术");
        typeToUpdate.setBookTypeDesc("更新后的描述");

        when(bookTypeRepository.existsById(1)).thenReturn(true);
        when(bookTypeRepository.save(any(BookType.class))).thenReturn(typeToUpdate);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(1, result);
        verify(bookTypeRepository, times(1)).save(any(BookType.class));
    }

    @Test
    void testUpdateBookType_NullId() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(null);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(0, result);
    }

    @Test
    void testUpdateBookType_NotFound() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(999);

        when(bookTypeRepository.existsById(999)).thenReturn(false);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(0, result);
    }

    @Test
    void testUpdateBookType_PresetType() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(1);
        typeToUpdate.setBookTypeName("计算机科学");
        typeToUpdate.setBookTypeDesc("包含编程、算法、数据结构等");

        when(bookTypeRepository.existsById(1)).thenReturn(true);
        when(bookTypeRepository.save(any(BookType.class))).thenReturn(typeToUpdate);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(1, result);
    }
}
