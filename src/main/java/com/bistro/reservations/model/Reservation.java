package com.bistro.reservations.model;

import com.bistro.reservations.eventstore.ReservationEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


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

    private void apply(ReservationEvent event){

        switch (event) {
            case ReservationEvent.Created c -> {
                this.id = c.reservationId();
                this.reservationCode = c.reservationCode();
                this.customerName = c.customerName();
                this.customerEmail = c.customerEmail();
                this.reservationTime = c.reservationTime();
                this.partySize = c.partySize();
                this.status = ReservationStatus.PENDING;
            }
            case ReservationEvent.Confirmed c -> {
                this.status = ReservationStatus.CONFIRMED;
                this.assignedTableId = c.tableId();
                this.tableNumber = c.tableNumber();
            }
            case ReservationEvent.Rejected r -> this.status = ReservationStatus.REJECTED;
            case ReservationEvent.Cancelled x -> this.status = ReservationStatus.CANCELLED;
        }
    }

    public static Reservation rebuild(List<ReservationEvent> events){
        Reservation reservation = new Reservation();
        events.forEach(reservation::apply);
        return reservation;
    }


}











