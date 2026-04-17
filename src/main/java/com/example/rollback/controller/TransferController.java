package com.example.rollback.controller;

import com.example.rollback.dto.ApiResponse;
import com.example.rollback.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping("/success")
    ApiResponse<String> success() {
        ApiResponse<String> successApi = new ApiResponse<>();
        successApi.setCode(100);
        successApi.setResult(service.transferSuccess("10", "1", 1000));
        return successApi;
    }

    @PostMapping("/no-transaction")
    ApiResponse<String> testNoTransaction(){
        ApiResponse<String> NoTransactionApi = new ApiResponse<>();
        NoTransactionApi.setCode(200);
        try {
            NoTransactionApi.setResult(service.transferWithoutTransaction("4", "5", 1000));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return NoTransactionApi;
    }

    @PostMapping("/checked")
    public String checked() throws Exception {
        service.transferSuccessButCheckedException("2", "3", 1000);
        return "CHECKED";
    }

    @PostMapping("/runtime")
    public String runtime() {
        service.transferFailRollbackRuntime("9", "8", 2000);
        return "RUNTIME";
    }

    @PostMapping("/checked-rollback")
    public String checkedRollback() throws Exception {
        service.transferRollbackChecked("8", "7", 5000);
        return "CHECKED ROLLBACK";
    }
}