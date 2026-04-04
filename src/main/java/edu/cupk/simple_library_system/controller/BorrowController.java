package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.BorrowView;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.entity.Borrow;
import edu.cupk.simple_library_system.repository.BookInfoRepository;
import edu.cupk.simple_library_system.repository.BorrowRepository;
import edu.cupk.simple_library_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/borrow")
public class BorrowController {
    private final BorrowRepository borrowRepository;
    private final BookInfoRepository bookInfoRepository;
    private final UserRepository userRepository;

    public BorrowController(BorrowRepository borrowRepository,
                            BookInfoRepository bookInfoRepository,
                            UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookInfoRepository = bookInfoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/getCount")
    public long getCount() {
        return borrowRepository.count();
    }

    @GetMapping("/queryBorrowsByPage")
    public PageResponse<BorrowView> queryBorrowsByPage(@RequestParam int page,
                                                       @RequestParam int limit,
                                                       @RequestParam(required = false) Integer userid,
                                                       @RequestParam(required = false) Integer bookid) {
        Page<Borrow> result = borrowRepository.search(userid, bookid, PageRequest.of(Math.max(page - 1, 0), limit));
        List<BorrowView> views = result.getContent().stream().map(this::toView).toList();
        return PageResponse.success(result.getTotalElements(), views);
    }

    @Transactional
    @RequestMapping(value = "/borrowBook", method = {RequestMethod.POST, RequestMethod.GET})
    public Integer borrowBook(@RequestParam Integer userid, @RequestParam Integer bookid) {
        if (!userRepository.existsById(userid)) {
            return 0;
        }
        Optional<BookInfo> optionalBook = bookInfoRepository.findById(bookid);
        if (optionalBook.isEmpty()) {
            return 0;
        }
        BookInfo book = optionalBook.get();
        if (book.getIsBorrowed() != null && book.getIsBorrowed() == 1) {
            return 0;
        }
        book.setIsBorrowed((byte) 1);
        bookInfoRepository.save(book);

        Borrow borrow = new Borrow();
        borrow.setUserId(userid);
        borrow.setBookId(bookid);
        borrow.setBorrowTime(LocalDateTime.now());
        borrowRepository.save(borrow);
        return 1;
    }

    @RequestMapping(value = "/reader/borrowBook", method = {RequestMethod.POST, RequestMethod.GET})
    public Integer readerBorrowBook(@RequestParam Integer userid, @RequestParam Integer bookid) {
        return borrowBook(userid, bookid);
    }

    @Transactional
    @RequestMapping(value = "/returnBook", method = {RequestMethod.POST, RequestMethod.GET})
    public Integer returnBook(@RequestParam Integer borrowid, @RequestParam Integer bookid) {
        Optional<Borrow> optionalBorrow = borrowRepository.findById(borrowid);
        Optional<BookInfo> optionalBook = bookInfoRepository.findById(bookid);
        if (optionalBorrow.isEmpty() || optionalBook.isEmpty()) {
            return 0;
        }
        Borrow borrow = optionalBorrow.get();
        if (borrow.getReturnTime() != null) {
            return 0;
        }
        borrow.setReturnTime(LocalDateTime.now());
        borrowRepository.save(borrow);

        BookInfo book = optionalBook.get();
        book.setIsBorrowed((byte) 0);
        bookInfoRepository.save(book);
        return 1;
    }

    @RequestMapping(value = "/reader/returnBook", method = {RequestMethod.POST, RequestMethod.GET})
    public Integer readerReturnBook(@RequestParam Integer borrowid, @RequestParam Integer bookid) {
        return returnBook(borrowid, bookid);
    }

    @PostMapping("/addBorrow")
    public Integer addBorrow(@RequestBody Borrow borrow) {
        if (borrow.getBorrowTime() == null) {
            borrow.setBorrowTime(LocalDateTime.now());
        }
        borrowRepository.save(borrow);
        return 1;
    }

    @DeleteMapping("/deleteBorrow")
    public Integer deleteBorrow(@RequestBody Borrow borrow) {
        if (borrow.getBorrowId() == null || !borrowRepository.existsById(borrow.getBorrowId())) {
            return 0;
        }
        borrowRepository.deleteById(borrow.getBorrowId());
        return 1;
    }

    @DeleteMapping("/deleteBorrows")
    public Integer deleteBorrows(@RequestBody List<Borrow> borrows) {
        int count = 0;
        for (Borrow item : borrows) {
            if (item.getBorrowId() != null && borrowRepository.existsById(item.getBorrowId())) {
                borrowRepository.deleteById(item.getBorrowId());
                count++;
            }
        }
        return count;
    }

    @PutMapping("/updateBorrow")
    public Integer updateBorrow(@RequestBody Borrow borrow) {
        if (borrow.getBorrowId() == null || !borrowRepository.existsById(borrow.getBorrowId())) {
            return 0;
        }
        borrowRepository.save(borrow);
        return 1;
    }

    private BorrowView toView(Borrow borrow) {
        BorrowView view = new BorrowView();
        view.setBorrowId(borrow.getBorrowId());
        view.setUserId(borrow.getUserId());
        view.setBookId(borrow.getBookId());
        view.setBorrowTime(borrow.getBorrowTime());
        view.setReturnTime(borrow.getReturnTime());
        userRepository.findById(borrow.getUserId()).ifPresent(user -> view.setUserName(user.getUserName()));
        bookInfoRepository.findById(borrow.getBookId()).ifPresent(book -> view.setBookName(book.getBookName()));
        return view;
    }
}
