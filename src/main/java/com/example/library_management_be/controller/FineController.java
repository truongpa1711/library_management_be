package com.example.library_management_be.controller;

import com.example.library_management_be.dto.BaseResponse;
import com.example.library_management_be.dto.request.FineFilterRequest;
import com.example.library_management_be.dto.response.FineResponse;
import com.example.library_management_be.entity.enums.EFineStatus;
import com.example.library_management_be.service.FineService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {
    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Page<FineResponse>>> getAllFines(
            @Valid FineFilterRequest filterRequest,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(fineService.getAllFines(filterRequest, pageable));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> updateFineStatus(
            @PathVariable Long id,
            @RequestParam EFineStatus status
    ) {
        return ResponseEntity.ok(fineService.updateFineStatus(id, status));
    }

    @GetMapping("/my-fines")
    public ResponseEntity<BaseResponse<?>> getMyFines(
            Authentication authentication,
            @ModelAttribute FineFilterRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // Ghi đè email theo tài khoản hiện tại
        request.setEmail(authentication.getName());
        return ResponseEntity.ok(fineService.getAllFines(request, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> deleteFine(@PathVariable Long id) {
        fineService.deleteFine(id);
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .status("success")
                .message("Fine deleted successfully")
                .data(null)
                .build());
    }



}
