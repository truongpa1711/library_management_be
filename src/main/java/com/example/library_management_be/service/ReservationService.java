package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.ReservationFilterRequest;
import com.example.library_management_be.dto.request.ReservationRequest;
import com.example.library_management_be.dto.response.ReservationResponse;
import com.example.library_management_be.entity.Book;
import com.example.library_management_be.entity.Reservation;
import com.example.library_management_be.entity.User;
import com.example.library_management_be.entity.enums.EReservationStatus;
import com.example.library_management_be.exception.BaseException;
import com.example.library_management_be.exception.BookException;
import com.example.library_management_be.exception.UserException;
import com.example.library_management_be.mapper.ReservationMapper;
import com.example.library_management_be.repository.BookRepository;
import com.example.library_management_be.repository.ReservationRepository;
import com.example.library_management_be.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository, UserRepository userRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reservationMapper = reservationMapper;
    }

    public BaseResponse<ReservationResponse> createReservation(ReservationRequest request, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("Không tìm thấy người dùng"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookException.BookNotFoundException("Không tìm thấy sách"));

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setReservationDate(LocalDate.now());
        reservation.setExpiryDate(LocalDate.now().plusDays(3)); // ví dụ: hết hạn sau 3 ngày
        reservation.setStatus(EReservationStatus.PENDING);
        reservation = reservationRepository.save(reservation);
        ReservationResponse response = reservationMapper.toResponse(reservation);
        return BaseResponse.<ReservationResponse>builder()
                .status("success")
                .message("Đặt trước thành công")
                .data(response)
                .build();
    }

    public BaseResponse<Page<ReservationResponse>> filterReservations(ReservationFilterRequest filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reservationDate"));

        Page<Reservation> result = reservationRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getUserEmail() != null && !filter.getUserEmail().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("user").get("email")), filter.getUserEmail().toLowerCase()));
            }

            if (filter.getBookTitle() != null && !filter.getBookTitle().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("book").get("title")), "%" + filter.getBookTitle().toLowerCase() + "%"));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("reservationDate"), filter.getFromDate()));
            }

            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("reservationDate"), filter.getToDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        Page<ReservationResponse> responsePage = result.map(reservationMapper::toResponse);
        return BaseResponse.<Page<ReservationResponse>>builder()
                .status("success")
                .message("Lấy danh sách đặt trước thành công")
                .data(responsePage)
                .build();
    }

    public BaseResponse<ReservationResponse> updateReservationStatus(Long id, EReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new BookException.BookNotFoundException("Không tìm thấy đặt trước"));

        if (reservation.getStatus() != EReservationStatus.PENDING && status == EReservationStatus.APPROVED) {
            throw new BaseException.CustomBadRequestException("Chỉ yêu cầu PENDING mới có thể duyệt");
        }

        if (reservation.getStatus() != EReservationStatus.APPROVED && status == EReservationStatus.FULFILLED) {
            throw new BaseException.CustomBadRequestException("Chỉ yêu cầu đã APPROVED mới có thể hoàn tất (FULFILLED)");
        }

        if (reservation.getStatus() != EReservationStatus.PENDING && status == EReservationStatus.CANCELLED) {
            throw new BaseException.CustomBadRequestException("Chỉ yêu cầu PENDING mới có thể từ chối");
        }

        reservation.setStatus(status);
        reservation = reservationRepository.save(reservation);

        ReservationResponse response = reservationMapper.toResponse(reservation);

        String message;
        switch (status) {
            case APPROVED -> message = "Duyệt đặt trước thành công";
            case CANCELLED -> message = "Từ chối đặt trước thành công";
            case FULFILLED -> message = "Hoàn tất đặt trước thành công";
            default -> message = "Cập nhật trạng thái thành công";
        }

        return BaseResponse.<ReservationResponse>builder()
                .status("success")
                .message(message)
                .data(response)
                .build();
    }

    public BaseResponse<String> cancelReservation(Long id, String email) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new BookException.BookNotFoundException("Không tìm thấy đặt trước"));

        if (!reservation.getUser().getEmail().equals(email)) {
            throw new BaseException.CustomBadRequestException("Bạn không có quyền hủy đặt trước này");
        }

        if (reservation.getStatus() != EReservationStatus.PENDING) {
            throw new BaseException.CustomBadRequestException("Chỉ yêu cầu PENDING mới có thể hủy");
        }

        reservation.setStatus(EReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return BaseResponse.<String>builder()
                .status("success")
                .message("Hủy đặt trước thành công")
                .data("Đặt trước đã được hủy")
                .build();
    }




}
