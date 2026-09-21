package com.bistro.reservations.model;

import lombok.*;

import java.time.LocalDateTime;


@Getter
public class Reservation {

    private Long id;
    private String reservationCode;
    private String customerName;
    private String customerEmail;
    private LocalDateTime reservationTime;
    private Integer partySize;
    private ReservationStatus status;
    private Long assignedTableId;
    private String tableNumber;




}











