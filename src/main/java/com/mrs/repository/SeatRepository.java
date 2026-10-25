package com.mrs.repository;

import com.mrs.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByTheater(String theater);

    /**
     * SELECT ... FOR UPDATE on the given seats. Serializes concurrent reservation
     * attempts for the same seats - the core defense against double-booking.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id in :ids")
    List<Seat> lockAllByIds(@Param("ids") List<Long> ids);
}
