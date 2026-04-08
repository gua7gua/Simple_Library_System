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

@SpringBootTest
class BorrowControllerTest {

    @Autowired
    private BorrowController borrowController;

    @MockitoBean
    private BorrowRepository borrowRepository;

    @MockitoBean
    private BookInfoRepository bookInfoRepository;

    @MockitoBean
    private UserRepository userRepository;

    private Borrow testBorrow;
    private BookInfo testBookInfo;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserName("testuser");
        testUser.setUserPassword("password");
        testUser.setIsAdmin((byte) 0);

        testBookInfo = new BookInfo();
        testBookInfo.setBookId(1);
        testBookInfo.setBookName("Java编程思想");
        testBookInfo.setBookAuthor("Bruce Eckel");
        testBookInfo.setBookPrice(new BigDecimal("108.00"));
        testBookInfo.setBookTypeId(1);
        testBookInfo.setBookDesc("经典的Java编程书籍");
        testBookInfo.setIsBorrowed((byte) 0);
        testBookInfo.setBookImg("/uploads/java.jpg");

        testBorrow = new Borrow();
        testBorrow.setBorrowId(1);
        testBorrow.setUserId(1);
        testBorrow.setBookId(1);
        testBorrow.setBorrowTime(LocalDateTime.now());
        testBorrow.setReturnTime(null);
    }

    @Test
    void testGetCount() {
        when(borrowRepository.count()).thenReturn(50L);

        long count = borrowController.getCount();

        assertEquals(50L, count);
    }

    @Test
    void testQueryBorrowsByPage() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(testBorrow);
        Page<Borrow> page = new PageImpl<>(borrows);

        when(borrowRepository.search(null, null, PageRequest.of(0, 10))).thenReturn(page);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        PageResponse<BorrowView> result = borrowController.queryBorrowsByPage(1, 10, null, null);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("testuser", result.getData().get(0).getUserName());
        assertEquals("Java编程思想", result.getData().get(0).getBookName());
    }

    @Test
    void testQueryBorrowsByPage_WithUserId() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(testBorrow);
        Page<Borrow> page = new PageImpl<>(borrows);

        when(borrowRepository.search(1, null, PageRequest.of(0, 10))).thenReturn(page);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        PageResponse<BorrowView> result = borrowController.queryBorrowsByPage(1, 10, 1, null);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().get(0).getUserId());
    }

    @Test
    void testQueryBorrowsByPage_WithBookId() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(testBorrow);
        Page<Borrow> page = new PageImpl<>(borrows);

        when(borrowRepository.search(null, 1, PageRequest.of(0, 10))).thenReturn(page);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        PageResponse<BorrowView> result = borrowController.queryBorrowsByPage(1, 10, null, 1);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().get(0).getBookId());
    }

    @Test
    void testQueryBorrowsByPage_WithMultipleConditions() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(testBorrow);
        Page<Borrow> page = new PageImpl<>(borrows);

        when(borrowRepository.search(1, 1, PageRequest.of(0, 10))).thenReturn(page);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        PageResponse<BorrowView> result = borrowController.queryBorrowsByPage(1, 10, 1, 1);

        assertEquals(1, result.getCount());
    }

    @Test
    void testBorrowBook_Success() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(testBookInfo);
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);

        Integer result = borrowController.borrowBook(1, 1);

        assertEquals(1, result);
        verify(bookInfoRepository, times(1)).save(any(BookInfo.class));
        verify(borrowRepository, times(1)).save(any(Borrow.class));
    }

    @Test
    void testBorrowBook_UserNotFound() {
        when(userRepository.existsById(999)).thenReturn(false);

        Integer result = borrowController.borrowBook(999, 1);

        assertEquals(0, result);
    }

    @Test
    void testBorrowBook_BookNotFound() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookInfoRepository.findById(999)).thenReturn(Optional.empty());

        Integer result = borrowController.borrowBook(1, 999);

        assertEquals(0, result);
    }

    @Test
    void testBorrowBook_BookAlreadyBorrowed() {
        when(userRepository.existsById(1)).thenReturn(true);

        BookInfo borrowedBook = new BookInfo();
        borrowedBook.setBookId(1);
        borrowedBook.setBookName("Java编程思想");
        borrowedBook.setIsBorrowed((byte) 1);

        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(borrowedBook));

        Integer result = borrowController.borrowBook(1, 1);

        assertEquals(0, result);
    }

    @Test
    void testReaderBorrowBook_Success() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(testBookInfo);
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);

        Integer result = borrowController.readerBorrowBook(1, 1);

        assertEquals(1, result);
    }

    @Test
    void testReturnBook_Success() {
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(testBookInfo);

        Integer result = borrowController.returnBook(1, 1);

        assertEquals(1, result);
        verify(borrowRepository, times(1)).save(any(Borrow.class));
        verify(bookInfoRepository, times(1)).save(any(BookInfo.class));
    }

    @Test
    void testReturnBook_BorrowNotFound() {
        when(borrowRepository.findById(999)).thenReturn(Optional.empty());
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        Integer result = borrowController.returnBook(999, 1);

        assertEquals(0, result);
    }

    @Test
    void testReturnBook_BookNotFound() {
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(bookInfoRepository.findById(999)).thenReturn(Optional.empty());

        Integer result = borrowController.returnBook(1, 999);

        assertEquals(0, result);
    }

    @Test
    void testReturnBook_AlreadyReturned() {
        Borrow returnedBorrow = new Borrow();
        returnedBorrow.setBorrowId(1);
        returnedBorrow.setUserId(1);
        returnedBorrow.setBookId(1);
        returnedBorrow.setBorrowTime(LocalDateTime.now());
        returnedBorrow.setReturnTime(LocalDateTime.now());

        when(borrowRepository.findById(1)).thenReturn(Optional.of(returnedBorrow));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));

        Integer result = borrowController.returnBook(1, 1);

        assertEquals(0, result);
    }

    @Test
    void testReaderReturnBook_Success() {
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(testBookInfo);

        Integer result = borrowController.readerReturnBook(1, 1);

        assertEquals(1, result);
    }

    @Test
    void testAddBorrow_Success() {
        Borrow newBorrow = new Borrow();
        newBorrow.setUserId(1);
        newBorrow.setBookId(1);

        when(borrowRepository.save(any(Borrow.class))).thenReturn(newBorrow);

        Integer result = borrowController.addBorrow(newBorrow);

        assertEquals(1, result);
        verify(borrowRepository, times(1)).save(any(Borrow.class));
    }

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

    @Test
    void testDeleteBorrow_Success() {
        Borrow borrowToDelete = new Borrow();
        borrowToDelete.setBorrowId(1);

        when(borrowRepository.existsById(1)).thenReturn(true);
        doNothing().when(borrowRepository).deleteById(1);

        Integer result = borrowController.deleteBorrow(borrowToDelete);

        assertEquals(1, result);
        verify(borrowRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteBorrow_NullId() {
        Borrow borrowToDelete = new Borrow();
        borrowToDelete.setBorrowId(null);

        Integer result = borrowController.deleteBorrow(borrowToDelete);

        assertEquals(0, result);
    }

    @Test
    void testDeleteBorrow_NotFound() {
        Borrow borrowToDelete = new Borrow();
        borrowToDelete.setBorrowId(999);

        when(borrowRepository.existsById(999)).thenReturn(false);

        Integer result = borrowController.deleteBorrow(borrowToDelete);

        assertEquals(0, result);
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
        borrow3.setBorrowId(999);
        borrowsToDelete.add(borrow3);

        when(borrowRepository.existsById(1)).thenReturn(true);
        when(borrowRepository.existsById(2)).thenReturn(true);
        when(borrowRepository.existsById(999)).thenReturn(false);
        doNothing().when(borrowRepository).deleteById(anyInt());

        Integer result = borrowController.deleteBorrows(borrowsToDelete);

        assertEquals(2, result);
        verify(borrowRepository, times(1)).deleteById(1);
        verify(borrowRepository, times(1)).deleteById(2);
        verify(borrowRepository, never()).deleteById(999);
    }

    @Test
    void testDeleteBorrows_WithNullId() {
        List<Borrow> borrowsToDelete = new ArrayList<>();

        Borrow borrow1 = new Borrow();
        borrow1.setBorrowId(null);
        borrowsToDelete.add(borrow1);

        Borrow borrow2 = new Borrow();
        borrow2.setBorrowId(1);
        borrowsToDelete.add(borrow2);

        when(borrowRepository.existsById(1)).thenReturn(true);
        doNothing().when(borrowRepository).deleteById(1);

        Integer result = borrowController.deleteBorrows(borrowsToDelete);

        assertEquals(1, result);
    }

    @Test
    void testUpdateBorrow_Success() {
        Borrow borrowToUpdate = new Borrow();
        borrowToUpdate.setBorrowId(1);
        borrowToUpdate.setUserId(1);
        borrowToUpdate.setBookId(1);
        borrowToUpdate.setBorrowTime(LocalDateTime.now());
        borrowToUpdate.setReturnTime(LocalDateTime.now());

        when(borrowRepository.existsById(1)).thenReturn(true);
        when(borrowRepository.save(any(Borrow.class))).thenReturn(borrowToUpdate);

        Integer result = borrowController.updateBorrow(borrowToUpdate);

        assertEquals(1, result);
        verify(borrowRepository, times(1)).save(any(Borrow.class));
    }

    @Test
    void testUpdateBorrow_NullId() {
        Borrow borrowToUpdate = new Borrow();
        borrowToUpdate.setBorrowId(null);

        Integer result = borrowController.updateBorrow(borrowToUpdate);

        assertEquals(0, result);
    }

    @Test
    void testUpdateBorrow_NotFound() {
        Borrow borrowToUpdate = new Borrow();
        borrowToUpdate.setBorrowId(999);

        when(borrowRepository.existsById(999)).thenReturn(false);

        Integer result = borrowController.updateBorrow(borrowToUpdate);

        assertEquals(0, result);
    }
}
