package edu.cupk.simple_library_system.repository;

import edu.cupk.simple_library_system.entity.BookInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookInfoRepository extends JpaRepository<BookInfo, Integer> {
    @Query("SELECT b FROM BookInfo b WHERE " +
            "(:bookName IS NULL OR b.bookName LIKE %:bookName%) AND " +
            "(:bookAuthor IS NULL OR b.bookAuthor LIKE %:bookAuthor%) AND " +
            "(:bookTypeId IS NULL OR b.bookTypeId = :bookTypeId)")
    Page<BookInfo> search(@Param("bookName") String bookName,
                          @Param("bookAuthor") String bookAuthor,
                          @Param("bookTypeId") Integer bookTypeId,
                          Pageable pageable);
}
