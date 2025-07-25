package com.example.library_management_be.service;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.FineFilterRequest;
import com.example.library_management_be.dto.response.FineResponse;
import com.example.library_management_be.entity.Fine;
import com.example.library_management_be.entity.enums.EFineStatus;
import com.example.library_management_be.exception.BaseException;
import com.example.library_management_be.mapper.FineMapper;
import com.example.library_management_be.repository.FineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FineService {
    private final FineRepository fineRepository;
    private final FineMapper fineMapper;

    public FineService(FineRepository fineRepository, FineMapper fineMapper) {
        this.fineRepository = fineRepository;
        this.fineMapper = fineMapper;
    }

    public BaseResponse<Page<FineResponse>> getAllFines(FineFilterRequest filter, Pageable pageable) {
        Specification<Fine> spec = (root, query, cb) -> cb.conjunction();

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("user").get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
        }

        if (filter.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), filter.getStatus()));
        }

        if (filter.getFromDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("issuedDate"), filter.getFromDate().atStartOfDay()));
        }

        if (filter.getToDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("issuedDate"), filter.getToDate().atTime(23, 59, 59)));
        }

        Page<Fine> page = fineRepository.findAll(spec, pageable);
        Page<FineResponse> responsePage = page.map(fineMapper::toResponse);

        return BaseResponse.<Page<FineResponse>>builder()
                .status("success")
                .message("Danh sách phiếu phạt đã lọc")
                .data(responsePage)
                .build();
    }

    public BaseResponse<String> updateFineStatus(Long id, EFineStatus status) {
        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new BaseException.CustomNotFoundException("Fine not found with id: " + id));

        fine.setStatus(status);
        fineRepository.save(fine);

        return BaseResponse.<String>builder()
                .status("success")
                .message("Cập nhật trạng thái phiếu phạt thành công")
                .data("Fine status updated to " + status)
                .build();
    }

    public void deleteFine(Long id) {
        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new BaseException.CustomNotFoundException("Fine not found with id: " + id));
        fineRepository.delete(fine);
    }

}
