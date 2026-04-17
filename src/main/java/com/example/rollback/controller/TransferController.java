package com.example.rollback.controller;

import com.example.rollback.dto.ApiResponse;
import com.example.rollback.dto.TransferInput;
import com.example.rollback.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping("/success")
    ApiResponse<String> success(@RequestBody TransferInput transfer) {
        ApiResponse<String> successApi = new ApiResponse<>();
        successApi.setCode(100);
        successApi.setResult(service.transferSuccess(transfer));
        return successApi;
    }

    @PostMapping("/no-transaction")
    ApiResponse<String> testNoTransaction(@RequestBody TransferInput transfer){
        ApiResponse<String> NoTransactionApi = new ApiResponse<>();
        NoTransactionApi.setCode(200);
        try {
            NoTransactionApi.setResult(service.transferWithoutTransaction(transfer));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return NoTransactionApi;
    }

    @PostMapping("/checked")
    public String checked(@RequestBody TransferInput transfer) throws Exception {
        service.transferSuccessButCheckedException(transfer);
        return "CHECKED";
    }

    @PostMapping("/runtime")
    public String runtime(@RequestBody TransferInput transfer) {
        service.transferFailRollbackRuntime(transfer);
        return "RUNTIME";
    }

    @PostMapping("/checked-rollback")
    public String checkedRollback(TransferInput transfer) throws Exception {
        service.transferRollbackChecked(transfer);
        return "CHECKED ROLLBACK";
    }
}