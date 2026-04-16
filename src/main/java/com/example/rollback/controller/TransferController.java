package com.example.rollback.controller;

import com.example.rollback.dto.ApiResponse;
import com.example.rollback.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping("/success")
    ApiResponse<String> success() {
        ApiResponse<String> successApi = new ApiResponse<>();
        successApi.setCode(100);
        successApi.setResult(service.transferSuccess("1", "2", 100));
        return successApi;
    }

    @PostMapping("/checked")
    public String checked() throws Exception {
        service.transferSuccessButCheckedException("1", "2", 100);
        return "CHECKED";
    }

    @PostMapping("/runtime")
    public String runtime() {
        service.transferFailRollbackRuntime("1", "2", 2000);
        return "RUNTIME";
    }

    @PostMapping("/checked-rollback")
    public String checkedRollback() throws Exception {
        service.transferRollbackChecked("1", "2", 100);
        return "CHECKED ROLLBACK";
    }
}