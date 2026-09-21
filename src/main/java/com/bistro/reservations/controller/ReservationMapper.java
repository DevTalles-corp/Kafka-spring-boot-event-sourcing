package com.bistro.reservations.controller;

import com.bistro.reservations.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationResponse toResponse(Reservation reservation);

    ReservationStatusResponse toStatusResponse(Reservation reservation);
}
