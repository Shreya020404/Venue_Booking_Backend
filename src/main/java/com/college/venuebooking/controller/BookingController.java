package com.college.venuebooking.controller;

import com.college.venuebooking.exception.ResourceNotFoundException;
import com.college.venuebooking.model.Booking;
import com.college.venuebooking.model.Venue;
import com.college.venuebooking.repository.BookingRepository;
import com.college.venuebooking.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VenueRepository venueRepository;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestParam Long venueId,
            @RequestParam String requesterName,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        LocalDateTime start;
        LocalDateTime end;

        // Validate date format
        try {
            start = LocalDateTime.parse(startTime);
            end = LocalDateTime.parse(endTime);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Invalid date format
        }

        if (end.isBefore(start)) {
            return ResponseEntity.badRequest().body(null); // End time must be after start time
        }

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        List<Booking> existingBookings = bookingRepository.findByVenueId(venueId);
        for (Booking booking : existingBookings) {
            if (booking.getStartTime().isBefore(end) && booking.getEndTime().isAfter(start)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Conflict: venue is already booked
            }
        }

        Booking booking = new Booking(requesterName, start, end, venue);
        Booking savedBooking = bookingRepository.save(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking); // Return 201 Created status
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no bookings found
        }
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<List<Booking>> getBookingsByVenue(@PathVariable Long venueId) {
        List<Booking> bookings = bookingRepository.findByVenueId(venueId);
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no bookings found
        }
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/requester/{requesterName}")
    public ResponseEntity<List<Booking>> getBookingsByRequesterName(@PathVariable String requesterName) {
        List<Booking> bookings = bookingRepository.findByRequesterName(requesterName);
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no bookings found
        }
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Booking> approveBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        booking.setApproved(true);
        Booking updatedBooking = bookingRepository.save(booking);
        return ResponseEntity.ok(updatedBooking);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Booking> rejectBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        booking.setApproved(false);
        Booking updatedBooking = bookingRepository.save(booking);
        return ResponseEntity.ok(updatedBooking);
    }
}
