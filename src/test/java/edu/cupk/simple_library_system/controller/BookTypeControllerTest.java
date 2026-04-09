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

        assertEquals(6L, count, "图书类型总数必须是6");
    }

    @Test
    void testQueryBookTypes() {
        when(bookTypeRepository.findAll()).thenReturn(presetBookTypes);

        List<BookType> result = bookTypeController.queryBookTypes();

        assertEquals(6, result.size(), "返回的图书类型数量必须是6");
        assertEquals("计算机科学", result.get(0).getBookTypeName(), "第一个类型名称必须是'计算机科学'");
        assertEquals("历史", result.get(1).getBookTypeName(), "第二个类型名称必须是'历史'");
        assertEquals("文学", result.get(2).getBookTypeName(), "第三个类型名称必须是'文学'");
        assertEquals("科幻", result.get(3).getBookTypeName(), "第四个类型名称必须是'科幻'");
        assertEquals("小说", result.get(4).getBookTypeName(), "第五个类型名称必须是'小说'");
        assertEquals("外语", result.get(5).getBookTypeName(), "第六个类型名称必须是'外语'");
    }

    @Test
    void testReaderQueryBookTypes() {
        when(bookTypeRepository.findAll()).thenReturn(presetBookTypes);

        List<BookType> result = bookTypeController.readerQueryBookTypes();

        assertEquals(6, result.size(), "读者查询返回的图书类型数量必须是6");
        assertEquals("计算机科学", result.get(0).getBookTypeName(), "第一个类型名称必须是'计算机科学'");
    }

    @Test
    void testQueryBookTypesByPage() {
        Page<BookType> page = new PageImpl<>(presetBookTypes);

        when(bookTypeRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, null);

        assertEquals(6, result.getCount(), "分页查询总记录数必须是6");
        assertEquals(6, result.getData().size(), "分页查询返回数据列表大小必须是6");
    }

    @Test
    void testQueryBookTypesByPage_WithBookTypeName() {
        List<BookType> filteredTypes = new ArrayList<>();
        filteredTypes.add(presetBookTypes.get(0));
        Page<BookType> page = new PageImpl<>(filteredTypes);

        when(bookTypeRepository.findByBookTypeNameContaining("计算机", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, "计算机");

        assertEquals(1, result.getCount(), "按名称过滤后总记录数必须是1");
        assertEquals(1, result.getData().size(), "按名称过滤后返回数据列表大小必须是1");
        assertEquals("计算机科学", result.getData().get(0).getBookTypeName(), "过滤结果类型名称必须是'计算机科学'");
    }

    @Test
    void testQueryBookTypesByPage_WithFuzzySearch() {
        List<BookType> filteredTypes = new ArrayList<>();
        filteredTypes.add(presetBookTypes.get(3));
        filteredTypes.add(presetBookTypes.get(4));
        Page<BookType> page = new PageImpl<>(filteredTypes);

        when(bookTypeRepository.findByBookTypeNameContaining("说", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, "说");

        assertEquals(2, result.getCount(), "模糊搜索'说'字应返回2条记录");
    }

    @Test
    void testAddBookType_Success() {
        BookType newType = new BookType();
        newType.setBookTypeName("艺术");
        newType.setBookTypeDesc("艺术相关书籍");

        when(bookTypeRepository.save(any(BookType.class))).thenReturn(newType);

        Integer result = bookTypeController.addBookType(newType);

        assertEquals(1, result, "添加图书类型成功时返回结果必须是1");
        verify(bookTypeRepository, times(1)).save(any(BookType.class));
    }

    @Test
    void testAddBookType_PresetType() {
        BookType newType = new BookType();
        newType.setBookTypeName("哲学");
        newType.setBookTypeDesc("哲学相关书籍");

        when(bookTypeRepository.save(any(BookType.class))).thenReturn(newType);

        Integer result = bookTypeController.addBookType(newType);

        assertEquals(1, result, "添加预设类型成功时返回结果必须是1");
    }

    @Test
    void testDeleteBookType_Success() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(1);

        when(bookTypeRepository.existsById(1)).thenReturn(true);
        doNothing().when(bookTypeRepository).deleteById(1);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(1, result, "删除图书类型成功时返回结果必须是1");
        verify(bookTypeRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteBookType_NullId() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(null);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(0, result, "类型ID为空时删除失败返回结果必须是0");
    }

    @Test
    void testDeleteBookType_NotFound() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(999);

        when(bookTypeRepository.existsById(999)).thenReturn(false);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(0, result, "图书类型不存在时删除失败返回结果必须是0");
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

        assertEquals(2, result, "批量删除时应成功删除2条存在的记录");
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

        assertEquals(1, result, "批量删除时应跳过ID为空的记录，只删除1条有效记录");
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

        assertEquals(1, result, "更新图书类型成功时返回结果必须是1");
        verify(bookTypeRepository, times(1)).save(any(BookType.class));
    }

    @Test
    void testUpdateBookType_NullId() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(null);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(0, result, "类型ID为空时更新失败返回结果必须是0");
    }

    @Test
    void testUpdateBookType_NotFound() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(999);

        when(bookTypeRepository.existsById(999)).thenReturn(false);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(0, result, "图书类型不存在时更新失败返回结果必须是0");
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

        assertEquals(1, result, "更新预设类型成功时返回结果必须是1");
    }
}
