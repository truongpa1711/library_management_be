package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.ReservationFilterRequest;
import com.example.library_management_be.dto.request.ReservationRequest;
import com.example.library_management_be.dto.request.ReservationStatusUpdateRequest;
import com.example.library_management_be.dto.response.ReservationResponse;
import com.example.library_management_be.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ReservationResponse>> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        BaseResponse<ReservationResponse> response = reservationService.createReservation(request, authentication);
        return ResponseEntity.ok(response);
    }

    //2. Lấy danh sách đặt trước của user
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<Page<ReservationResponse>>> getUserReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            ReservationFilterRequest filter,
            Authentication authentication) {
        filter.setUserEmail(authentication.getName());
        return ResponseEntity.ok(reservationService.filterReservations( filter, page, size));
    }

    //Lấy danh sách tất cả yêu cầu đặt trước (cho admin)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Page<ReservationResponse>>> getAllReservationsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            ReservationFilterRequest filter) {

        return ResponseEntity.ok(reservationService.filterReservations(filter, page, size));
    }

    @PutMapping("/admin/update-status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ReservationResponse>> updateReservationStatus(
            @PathVariable Long id,
            @RequestBody ReservationStatusUpdateRequest request
    ) {
        BaseResponse<ReservationResponse> response = reservationService.updateReservationStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }

    //Huỷ đặt trước (do người dùng)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<String>> cancelReservation(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        BaseResponse<String> response = reservationService.cancelReservation(id, email);
        return ResponseEntity.ok(response);
    }
}
