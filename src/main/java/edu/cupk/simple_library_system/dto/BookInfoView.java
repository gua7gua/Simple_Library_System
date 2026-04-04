package edu.cupk.simple_library_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cupk.simple_library_system.entity.BookInfo;

import java.math.BigDecimal;

public class BookInfoView {
    @JsonProperty("bookid")
    private Integer bookId;
    @JsonProperty("bookname")
    private String bookName;
    @JsonProperty("bookauthor")
    private String bookAuthor;
    @JsonProperty("bookprice")
    private BigDecimal bookPrice;
    @JsonProperty("booktypeid")
    private Integer bookTypeId;
    @JsonProperty("booktypename")
    private String bookTypeName;
    @JsonProperty("bookdesc")
    private String bookDesc;
    @JsonProperty("isborrowed")
    private Byte isBorrowed;
    @JsonProperty("bookimg")
    private String bookImg;

    public static BookInfoView from(BookInfo entity, String bookTypeName) {
        BookInfoView view = new BookInfoView();
        view.bookId = entity.getBookId();
        view.bookName = entity.getBookName();
        view.bookAuthor = entity.getBookAuthor();
        view.bookPrice = entity.getBookPrice();
        view.bookTypeId = entity.getBookTypeId();
        view.bookTypeName = bookTypeName;
        view.bookDesc = entity.getBookDesc();
        view.isBorrowed = entity.getIsBorrowed();
        view.bookImg = entity.getBookImg();
        return view;
    }

    public Integer getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public BigDecimal getBookPrice() {
        return bookPrice;
    }

    public Integer getBookTypeId() {
        return bookTypeId;
    }

    public String getBookTypeName() {
        return bookTypeName;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public Byte getIsBorrowed() {
        return isBorrowed;
    }

    public String getBookImg() {
        return bookImg;
    }
}
