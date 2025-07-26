package com.example.library_management_be.dto.request;

import com.example.library_management_be.entity.enums.EReservationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationStatusUpdateRequest {
    private EReservationStatus status;
}
