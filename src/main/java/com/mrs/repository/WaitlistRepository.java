package com.mrs.repository;

import com.mrs.entity.WaitlistEntry;
import com.mrs.entity.WaitlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaitlistRepository extends JpaRepository<WaitlistEntry, Long> {

    List<WaitlistEntry> findByShowtimeIdAndStatusOrderByCreatedAtAsc(Long showtimeId, WaitlistStatus status);

    List<WaitlistEntry> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select distinct w.showtime.id from WaitlistEntry w where w.status = :status")
    List<Long> findShowtimeIdsByStatus(@Param("status") WaitlistStatus status);
}