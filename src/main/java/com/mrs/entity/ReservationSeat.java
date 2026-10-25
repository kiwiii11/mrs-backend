package com.mrs.entity;

import jakarta.persistence.*;
import lombok.*;

/** Junction table resolving the Reservation <-> Seat many-to-many relationship. */
@Entity
@Table(name = "reservation_seat")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
}
