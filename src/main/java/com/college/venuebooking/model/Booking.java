package com.college.venuebooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requesterName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    private boolean approved; // Add this field

    // Default constructor
    public Booking() {
    }

    // Constructor
    public Booking(String requesterName, LocalDateTime startTime, LocalDateTime endTime, Venue venue) {
        this.requesterName = requesterName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.approved = false; // Default value for approved
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) { // Add this setter
        this.approved = approved;
    }
}
