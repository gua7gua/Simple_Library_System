package edu.cupk.simple_library_system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "book_info")
public class BookInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookId")
    @JsonProperty("bookid")
    private Integer bookId;

    @Column(name = "bookName", nullable = false)
    @JsonProperty("bookname")
    private String bookName;

    @Column(name = "bookAuthor", nullable = false)
    @JsonProperty("bookauthor")
    private String bookAuthor;

    @Column(name = "bookPrice", nullable = false)
    @JsonProperty("bookprice")
    private BigDecimal bookPrice;

    @Column(name = "bookTypeId", nullable = false)
    @JsonProperty("booktypeid")
    private Integer bookTypeId;

    @Column(name = "bookDesc", nullable = false)
    @JsonProperty("bookdesc")
    private String bookDesc;

    @Column(name = "isBorrowed", nullable = false)
    @JsonProperty("isborrowed")
    private Byte isBorrowed;

    @Column(name = "bookImg")
    @JsonProperty("bookimg")
    private String bookImg;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public BigDecimal getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(BigDecimal bookPrice) {
        this.bookPrice = bookPrice;
    }

    public Integer getBookTypeId() {
        return bookTypeId;
    }

    public void setBookTypeId(Integer bookTypeId) {
        this.bookTypeId = bookTypeId;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public Byte getIsBorrowed() {
        return isBorrowed;
    }

    public void setIsBorrowed(Byte isBorrowed) {
        this.isBorrowed = isBorrowed;
    }

    public String getBookImg() {
        return bookImg;
    }

    public void setBookImg(String bookImg) {
        this.bookImg = bookImg;
    }
}
