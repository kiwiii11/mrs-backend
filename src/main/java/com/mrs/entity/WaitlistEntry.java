package com.mrs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "waitlist_entries")
@Getter @Setter @NoArgsConstructor
public class WaitlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Showtime showtime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "waitlist_entry_seats", joinColumns = @JoinColumn(name = "waitlist_entry_id"))
    @Column(name = "seat_id")
    private List<Long> seatIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private WaitlistStatus status = WaitlistStatus.WAITING;

    private LocalDateTime createdAt = LocalDateTime.now();
}