package com.mrs.repository;

import com.mrs.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    @Query("""
           select rs.seat.id from ReservationSeat rs
           where rs.reservation.showtime.id = :showtimeId
             and rs.reservation.status = com.mrs.entity.ReservationStatus.CONFIRMED
             and rs.seat.id in :seatIds
           """)
    List<Long> findBookedSeatIds(@Param("showtimeId") Long showtimeId, @Param("seatIds") List<Long> seatIds);

    @Query("""
           select rs.seat.id from ReservationSeat rs
           where rs.reservation.showtime.id = :showtimeId
             and rs.reservation.status = com.mrs.entity.ReservationStatus.CONFIRMED
           """)
    List<Long> findAllBookedSeatIdsForShowtime(@Param("showtimeId") Long showtimeId);
}
