package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.BorrowView;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.entity.Borrow;
import edu.cupk.simple_library_system.entity.User;
import edu.cupk.simple_library_system.repository.BookInfoRepository;
import edu.cupk.simple_library_system.repository.BorrowRepository;
import edu.cupk.simple_library_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// 借阅管理控制器测试类
// 测试BorrowController中的所有借阅管理相关功能
// 包括：借书、还书、借阅记录查询、借阅记录CRUD操作等
@SpringBootTest
class BorrowControllerTest {

    // 自动注入被测试的控制器实例
    @Autowired
    private BorrowController borrowController;

    // 模拟借阅记录数据访问层，用于隔离数据库依赖
    @MockitoBean
    private BorrowRepository borrowRepository;

    // 模拟图书信息数据访问层，用于隔离数据库依赖
    @MockitoBean
    private BookInfoRepository bookInfoRepository;

    // 模拟用户数据访问层，用于隔离数据库依赖
    @MockitoBean
    private UserRepository userRepository;

    // 测试用的借阅记录实体对象
    private Borrow testBorrow;

    // 测试用的图书信息实体对象
    private BookInfo testBookInfo;

    // 测试用的用户实体对象
    private User testUser;

    // 每个测试方法执行前的初始化操作
    // 创建测试用的借阅记录、图书信息和用户数据
    @BeforeEach
    void setUp() {
        // 初始化测试用户数据
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserName("testuser");
        testUser.setUserPassword("password");
        testUser.setIsAdmin((byte) 0);

        // 初始化测试图书信息数据（未借阅状态）
        testBookInfo = new BookInfo();
        testBookInfo.setBookId(1);
        testBookInfo.setBookName("Java编程思想");
        testBookInfo.setBookAuthor("Bruce Eckel");
        testBookInfo.setBookPrice(new BigDecimal("108.00"));
        testBookInfo.setBookTypeId(1);
        testBookInfo.setBookDesc("经典的Java编程书籍");
        testBookInfo.setIsBorrowed((byte) 0);
        testBookInfo.setBookImg("/uploads/java.jpg");

        // 初始化测试借阅记录数据
        testBorrow = new Borrow();
        testBorrow.setBorrowId(1);
        testBorrow.setUserId(1);
        testBorrow.setBookId(1);
        testBorrow.setBorrowTime(LocalDateTime.now());
        testBorrow.setReturnTime(null);
    }

    // 测试获取借阅记录总数
    // 验证点：
    // 1. 返回的借阅记录数量与模拟值一致
    @Test
    void testGetCount() {
        // 模拟借阅记录数量为50
        when(borrowRepository.count()).thenReturn(50L);

        long count = borrowController.getCount();

        assertEquals(50L, count);
    }

    // 测试分页查询借阅记录（不带过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 返回的数据列表大小正确
    // 3. 用户名和图书名正确转换
    @Test
    void testQueryBorrowsByPage() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(testBorrow);
        Page<Borrow> page = new PageImpl<>(borrows);

        // 模拟分页查询
        when(borrowRepository.search(null, null, PageRequest.of(0, 10))).thenReturn(page);
        // 模拟查询用户和图书信息
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        PageResponse<BorrowView> result = borrowController.queryBorrowsByPage(1, 10, null, null);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("testuser", result.getData().get(0).getUserName());
        assertEquals("Java编程思想", result.getData().get(0).getBookName());
    }

    // 测试分页查询借阅记录（带用户ID过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 用户ID过滤条件正确传递
    @Test
    void testQueryBorrowsByPage_WithUserId() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(testBorrow);
        Page<Borrow> page = new PageImpl<>(borrows);

        // 模拟按用户ID搜索
        when(borrowRepository.search(1, null, PageRequest.of(0, 10))).thenReturn(page);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        PageResponse<BorrowView> result = borrowController.queryBorrowsByPage(1, 10, 1, null);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().get(0).getUserId());
    }

    // 测试分页查询借阅记录（带图书ID过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 图书ID过滤条件正确传递
    @Test
    void testQueryBorrowsByPage_WithBookId() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(testBorrow);
        Page<Borrow> page = new PageImpl<>(borrows);

        // 模拟按图书ID搜索
        when(borrowRepository.search(null, 1, PageRequest.of(0, 10))).thenReturn(page);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        PageResponse<BorrowView> result = borrowController.queryBorrowsByPage(1, 10, null, 1);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().get(0).getBookId());
    }

    // 测试分页查询借阅记录（带多个过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 多个搜索条件同时生效
    @Test
    void testQueryBorrowsByPage_WithMultipleConditions() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(testBorrow);
        Page<Borrow> page = new PageImpl<>(borrows);

        // 模拟多条件搜索
        when(borrowRepository.search(1, 1, PageRequest.of(0, 10))).thenReturn(page);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        PageResponse<BorrowView> result = borrowController.queryBorrowsByPage(1, 10, 1, 1);

        assertEquals(1, result.getCount());
    }

    // 测试借书成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 用户存在
    // 3. 图书存在且未被借阅
    // 4. 图书状态更新为已借阅
    // 5. 借阅记录被创建
    @Test
    void testBorrowBook_Success() {
        // 模拟用户存在
        when(userRepository.existsById(1)).thenReturn(true);
        // 模拟图书存在且未被借阅
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        // 模拟保存图书信息
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(testBookInfo);
        // 模拟保存借阅记录
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);

        Integer result = borrowController.borrowBook(1, 1);

        assertEquals(1, result);
        verify(bookInfoRepository, times(1)).save(any(BookInfo.class));
        verify(borrowRepository, times(1)).save(any(Borrow.class));
    }

    // 测试借书失败场景 - 用户不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testBorrowBook_UserNotFound() {
        // 模拟用户不存在
        when(userRepository.existsById(999)).thenReturn(false);

        Integer result = borrowController.borrowBook(999, 1);

        assertEquals(0, result);
    }

    // 测试借书失败场景 - 图书不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testBorrowBook_BookNotFound() {
        // 模拟用户存在
        when(userRepository.existsById(1)).thenReturn(true);
        // 模拟图书不存在
        when(bookInfoRepository.findById(999)).thenReturn(Optional.empty());

        Integer result = borrowController.borrowBook(1, 999);

        assertEquals(0, result);
    }

    // 测试借书失败场景 - 图书已被借阅
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 2. 已被借阅的图书不能再次借阅
    @Test
    void testBorrowBook_BookAlreadyBorrowed() {
        // 模拟用户存在
        when(userRepository.existsById(1)).thenReturn(true);

        // 创建已被借阅的图书
        BookInfo borrowedBook = new BookInfo();
        borrowedBook.setBookId(1);
        borrowedBook.setBookName("Java编程思想");
        borrowedBook.setIsBorrowed((byte) 1);

        // 模拟图书存在但已被借阅
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(borrowedBook));

        Integer result = borrowController.borrowBook(1, 1);

        assertEquals(0, result);
    }

    // 测试读者借书接口
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 功能与管理员借书相同
    @Test
    void testReaderBorrowBook_Success() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(testBookInfo);
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);

        Integer result = borrowController.readerBorrowBook(1, 1);

        assertEquals(1, result);
    }

    // 测试还书成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 借阅记录存在且未归还
    // 3. 图书存在
    // 4. 借阅记录的归还时间被设置
    // 5. 图书状态更新为未借阅
    @Test
    void testReturnBook_Success() {
        // 模拟借阅记录存在且未归还
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        // 模拟图书存在
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        // 模拟保存借阅记录
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);
        // 模拟保存图书信息
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(testBookInfo);

        Integer result = borrowController.returnBook(1, 1);

        assertEquals(1, result);
        verify(borrowRepository, times(1)).save(any(Borrow.class));
        verify(bookInfoRepository, times(1)).save(any(BookInfo.class));
    }

    // 测试还书失败场景 - 借阅记录不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testReturnBook_BorrowNotFound() {
        // 模拟借阅记录不存在
        when(borrowRepository.findById(999)).thenReturn(Optional.empty());
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        Integer result = borrowController.returnBook(999, 1);

        assertEquals(0, result);
    }

    // 测试还书失败场景 - 图书不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testReturnBook_BookNotFound() {
        // 模拟借阅记录存在
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        // 模拟图书不存在
        when(bookInfoRepository.findById(999)).thenReturn(Optional.empty());

        Integer result = borrowController.returnBook(1, 999);

        assertEquals(0, result);
    }

    // 测试还书失败场景 - 图书已归还
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 2. 已归还的图书不能再次归还
    @Test
    void testReturnBook_AlreadyReturned() {
        // 创建已归还的借阅记录
        Borrow returnedBorrow = new Borrow();
        returnedBorrow.setBorrowId(1);
        returnedBorrow.setUserId(1);
        returnedBorrow.setBookId(1);
        returnedBorrow.setBorrowTime(LocalDateTime.now());
        returnedBorrow.setReturnTime(LocalDateTime.now());

        // 模拟借阅记录存在但已归还
        when(borrowRepository.findById(1)).thenReturn(Optional.of(returnedBorrow));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        Integer result = borrowController.returnBook(1, 1);

        assertEquals(0, result);
    }

    // 测试读者还书接口
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 功能与管理员还书相同
    @Test
    void testReaderReturnBook_Success() {
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(testBookInfo);

        Integer result = borrowController.readerReturnBook(1, 1);

        assertEquals(1, result);
    }

    // 测试添加借阅记录成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 借阅记录被正确保存
    @Test
    void testAddBorrow_Success() {
        Borrow newBorrow = new Borrow();
        newBorrow.setUserId(1);
        newBorrow.setBookId(1);
        // 不设置借阅时间，测试自动设置

        // 模拟保存借阅记录
        when(borrowRepository.save(any(Borrow.class))).thenReturn(newBorrow);

        Integer result = borrowController.addBorrow(newBorrow);

        assertEquals(1, result);
        verify(borrowRepository, times(1)).save(any(Borrow.class));
    }

    // 测试添加借阅记录（已设置借阅时间）
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 保留设置的借阅时间
    @Test
    void testAddBorrow_WithBorrowTime() {
        Borrow newBorrow = new Borrow();
        newBorrow.setUserId(1);
        newBorrow.setBookId(1);
        newBorrow.setBorrowTime(LocalDateTime.of(2024, 1, 1, 10, 0));

        when(borrowRepository.save(any(Borrow.class))).thenReturn(newBorrow);

        Integer result = borrowController.addBorrow(newBorrow);

        assertEquals(1, result);
    }

    // 测试删除借阅记录成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 借阅记录存在且被删除
    @Test
    void testDeleteBorrow_Success() {
        Borrow borrowToDelete = new Borrow();
        borrowToDelete.setBorrowId(1);

        // 模拟借阅记录存在
        when(borrowRepository.existsById(1)).thenReturn(true);
        // 模拟删除借阅记录
        doNothing().when(borrowRepository).deleteById(1);

        Integer result = borrowController.deleteBorrow(borrowToDelete);

        assertEquals(1, result);
        verify(borrowRepository, times(1)).deleteById(1);
    }

    // 测试删除借阅记录失败场景 - 借阅ID为空
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testDeleteBorrow_NullId() {
        Borrow borrowToDelete = new Borrow();
        borrowToDelete.setBorrowId(null);

        Integer result = borrowController.deleteBorrow(borrowToDelete);

        assertEquals(0, result);
    }

    // 测试删除借阅记录失败场景 - 借阅记录不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testDeleteBorrow_NotFound() {
        Borrow borrowToDelete = new Borrow();
        borrowToDelete.setBorrowId(999);

        // 模拟借阅记录不存在
        when(borrowRepository.existsById(999)).thenReturn(false);

        Integer result = borrowController.deleteBorrow(borrowToDelete);

        assertEquals(0, result);
    }

    // 测试批量删除借阅记录
    // 验证点：
    // 1. 返回成功删除的借阅记录数量
    // 2. 只删除存在的借阅记录
    @Test
    void testDeleteBorrows() {
        List<Borrow> borrowsToDelete = new ArrayList<>();

        // 第一条：存在，可以删除
        Borrow borrow1 = new Borrow();
        borrow1.setBorrowId(1);
        borrowsToDelete.add(borrow1);

        // 第二条：存在，可以删除
        Borrow borrow2 = new Borrow();
        borrow2.setBorrowId(2);
        borrowsToDelete.add(borrow2);

        // 第三条：不存在，不能删除
        Borrow borrow3 = new Borrow();
        borrow3.setBorrowId(999);
        borrowsToDelete.add(borrow3);

        // 模拟查询借阅记录是否存在
        when(borrowRepository.existsById(1)).thenReturn(true);
        when(borrowRepository.existsById(2)).thenReturn(true);
        when(borrowRepository.existsById(999)).thenReturn(false);

        // 模拟删除操作
        doNothing().when(borrowRepository).deleteById(anyInt());

        Integer result = borrowController.deleteBorrows(borrowsToDelete);

        assertEquals(2, result); // 只有2条存在的记录被删除
        verify(borrowRepository, times(1)).deleteById(1);
        verify(borrowRepository, times(1)).deleteById(2);
        verify(borrowRepository, never()).deleteById(999); // 不存在的不删除
    }

    // 测试批量删除借阅记录 - 包含ID为空的记录
    // 验证点：
    // 1. 跳过ID为空的记录
    // 2. 只删除有效的记录
    @Test
    void testDeleteBorrows_WithNullId() {
        List<Borrow> borrowsToDelete = new ArrayList<>();

        // 第一条：ID为空，跳过
        Borrow borrow1 = new Borrow();
        borrow1.setBorrowId(null);
        borrowsToDelete.add(borrow1);

        // 第二条：有效，可以删除
        Borrow borrow2 = new Borrow();
        borrow2.setBorrowId(1);
        borrowsToDelete.add(borrow2);

        when(borrowRepository.existsById(1)).thenReturn(true);
        doNothing().when(borrowRepository).deleteById(1);

        Integer result = borrowController.deleteBorrows(borrowsToDelete);

        assertEquals(1, result); // 只有1条有效记录被删除
    }

    // 测试更新借阅记录成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 借阅记录存在，可以正常更新
    @Test
    void testUpdateBorrow_Success() {
        Borrow borrowToUpdate = new Borrow();
        borrowToUpdate.setBorrowId(1);
        borrowToUpdate.setUserId(1);
        borrowToUpdate.setBookId(1);
        borrowToUpdate.setBorrowTime(LocalDateTime.now());
        borrowToUpdate.setReturnTime(LocalDateTime.now());

        // 模拟借阅记录存在
        when(borrowRepository.existsById(1)).thenReturn(true);
        // 模拟保存借阅记录
        when(borrowRepository.save(any(Borrow.class))).thenReturn(borrowToUpdate);

        Integer result = borrowController.updateBorrow(borrowToUpdate);

        assertEquals(1, result);
        verify(borrowRepository, times(1)).save(any(Borrow.class));
    }

    // 测试更新借阅记录失败场景 - 借阅ID为空
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testUpdateBorrow_NullId() {
        Borrow borrowToUpdate = new Borrow();
        borrowToUpdate.setBorrowId(null);

        Integer result = borrowController.updateBorrow(borrowToUpdate);

        assertEquals(0, result);
    }

    // 测试更新借阅记录失败场景 - 借阅记录不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testUpdateBorrow_NotFound() {
        Borrow borrowToUpdate = new Borrow();
        borrowToUpdate.setBorrowId(999);

        // 模拟借阅记录不存在
        when(borrowRepository.existsById(999)).thenReturn(false);

        Integer result = borrowController.updateBorrow(borrowToUpdate);

        assertEquals(0, result);
    }
}