package com.mrs.repository;

import com.mrs.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    @Query("""
           select count(rs) from ReservationSeat rs
           where rs.reservation.showtime.id = :showtimeId
             and rs.reservation.status = com.mrs.entity.ReservationStatus.CONFIRMED
           """)
    long countBookedSeatsForShowtime(@Param("showtimeId") Long showtimeId);
}
