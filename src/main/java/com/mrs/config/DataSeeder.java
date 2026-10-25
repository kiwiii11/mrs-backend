package com.mrs.config;

import com.mrs.entity.*;
import com.mrs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** On first startup: creates an admin account, demo seats for 2 screens, and a sample movie + showtime. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PasswordEncoder passwordEncoder;

    // not final on purpose: @Value fills these in. On the live server, set ADMIN_EMAIL / ADMIN_PASSWORD.
    @Value("${ADMIN_EMAIL:admin@mrs.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            userRepository.save(User.builder().name("Admin").email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword)).role(Role.ADMIN).build());
        }

        if (seatRepository.count() == 0) {
            List<Seat> seats = new ArrayList<>();
            for (String theater : List.of("Screen 1", "Screen 2")) {
                for (char row = 'A'; row <= 'E'; row++) {
                    for (int n = 1; n <= 10; n++) {
                        seats.add(Seat.builder().theater(theater).rowLabel(String.valueOf(row)).seatNumber(n).build());
                    }
                }
            }
            seatRepository.saveAll(seats);
        }

        if (movieRepository.count() == 0) {
            Movie movie = movieRepository.save(Movie.builder().title("Inception")
                    .description("A thief who steals secrets through dreams takes on one last job.")
                    .genre("Sci-Fi").duration(148).build());
            showtimeRepository.save(Showtime.builder().movie(movie).theater("Screen 1")
                    .startTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0))
                    .price(new BigDecimal("250.00")).build());
        }
    }
}