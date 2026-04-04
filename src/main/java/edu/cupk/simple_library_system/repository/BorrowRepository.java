package edu.cupk.simple_library_system.repository;

import edu.cupk.simple_library_system.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BorrowRepository extends JpaRepository<Borrow, Integer> {
    @Query("SELECT b FROM Borrow b WHERE " +
            "(:userId IS NULL OR b.userId = :userId) AND " +
            "(:bookId IS NULL OR b.bookId = :bookId)")
    Page<Borrow> search(@Param("userId") Integer userId,
                        @Param("bookId") Integer bookId,
                        Pageable pageable);
}
