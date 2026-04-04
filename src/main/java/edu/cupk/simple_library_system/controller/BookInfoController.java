package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.BookInfoView;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.repository.BookInfoRepository;
import edu.cupk.simple_library_system.repository.BookTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookInfo")
public class BookInfoController {
    private final BookInfoRepository bookInfoRepository;
    private final BookTypeRepository bookTypeRepository;

    public BookInfoController(BookInfoRepository bookInfoRepository, BookTypeRepository bookTypeRepository) {
        this.bookInfoRepository = bookInfoRepository;
        this.bookTypeRepository = bookTypeRepository;
    }

    @GetMapping("/getCount")
    public long getCount() {
        return bookInfoRepository.count();
    }

    @GetMapping("/queryBookInfos")
    public List<BookInfoView> queryBookInfos() {
        return bookInfoRepository.findAll().stream().map(this::toView).toList();
    }

    @GetMapping("/queryBookInfosByPage")
    public PageResponse<BookInfoView> queryBookInfosByPage(@RequestParam int page,
                                                           @RequestParam int limit,
                                                           @RequestParam(required = false) String bookname,
                                                           @RequestParam(required = false) String bookauthor,
                                                           @RequestParam(required = false) Integer booktypeid) {
        Page<BookInfo> result = bookInfoRepository.search(
                emptyToNull(bookname),
                emptyToNull(bookauthor),
                booktypeid,
                PageRequest.of(Math.max(page - 1, 0), limit)
        );
        List<BookInfoView> list = result.getContent().stream().map(this::toView).toList();
        return PageResponse.success(result.getTotalElements(), list);
    }

    @PostMapping("/addBookInfo")
    public Integer addBookInfo(@RequestBody BookInfo bookInfo) {
        if (bookInfo.getIsBorrowed() == null) {
            bookInfo.setIsBorrowed((byte) 0);
        }
        bookInfoRepository.save(bookInfo);
        return 1;
    }

    @DeleteMapping("/deleteBookInfo")
    public Integer deleteBookInfo(@RequestBody BookInfo bookInfo) {
        if (bookInfo.getBookId() == null) {
            return 0;
        }
        return bookInfoRepository.findById(bookInfo.getBookId()).map(target -> {
            if (target.getIsBorrowed() != null && target.getIsBorrowed() == 1) {
                return 0;
            }
            bookInfoRepository.deleteById(target.getBookId());
            return 1;
        }).orElse(0);
    }

    @DeleteMapping("/deleteBookInfos")
    public Integer deleteBookInfos(@RequestBody List<BookInfo> bookInfos) {
        int count = 0;
        for (BookInfo item : bookInfos) {
            if (item.getBookId() == null) {
                continue;
            }
            BookInfo target = bookInfoRepository.findById(item.getBookId()).orElse(null);
            if (target != null && (target.getIsBorrowed() == null || target.getIsBorrowed() == 0)) {
                bookInfoRepository.deleteById(target.getBookId());
                count++;
            }
        }
        return count;
    }

    @PutMapping("/updateBookInfo")
    public Integer updateBookInfo(@RequestBody BookInfo bookInfo) {
        if (bookInfo.getBookId() == null || !bookInfoRepository.existsById(bookInfo.getBookId())) {
            return 0;
        }
        bookInfoRepository.save(bookInfo);
        return 1;
    }

    private BookInfoView toView(BookInfo info) {
        String typeName = bookTypeRepository.findById(info.getBookTypeId())
                .map(t -> t.getBookTypeName())
                .orElse(null);
        return BookInfoView.from(info, typeName);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
