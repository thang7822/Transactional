package com.example.rollback.service;

import com.example.rollback.entity.Account;
import com.example.rollback.dto.TransferInput;
import com.example.rollback.exception.CustomException;
import com.example.rollback.exception.ErrorCode;
import com.example.rollback.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository repo;

    @Transactional
    public String transferSuccess( TransferInput transfer) {

        log.info("======== START SUCCESS ========");

        Account from = repo.findById(transfer.getFrom()).orElseThrow();
        Account to = repo.findById(transfer.getTo()).orElseThrow();

        from.setBalance(from.getBalance() - transfer.getAmount());
        to.setBalance(to.getBalance() + transfer.getAmount());

        log.info("======== END SUCCESS ========");
        return "SUCCESS";
    }

    public String transferWithoutTransaction( TransferInput transfer) throws SQLException {
        log.info("======== START NO TRANSACTION ========");

        Account from = repo.findById(transfer.getFrom()).orElseThrow();
        Account to = repo.findById(transfer.getTo()).orElseThrow();

        from.setBalance(from.getBalance() - transfer.getAmount());
        repo.save(from);

        boolean isDatabaseError = true;
        if (isDatabaseError) {
            throw new CustomException(ErrorCode.SQLException);
        }

        to.setBalance(to.getBalance() + transfer.getAmount());
        repo.save(to);

        log.info("======== END NO TRANSACTION ========");
        return "transfer Without Transaction";
    }

    @Transactional
    public void transferSuccessButCheckedException(TransferInput transfer) throws Exception {

        log.info("======== START TRANSACTION (DEMO CHECKED EXCEPTION ISSUE) ========");

        Account from = repo.findById(transfer.getFrom()).orElseThrow();
        Account to = repo.findById(transfer.getTo()).orElseThrow();

        from.setBalance(from.getBalance() - transfer.getAmount());
        repo.save(from);

        boolean isDatabaseError = true;
        if (isDatabaseError) {
            throw new SQLException();
        }

        to.setBalance(to.getBalance() + transfer.getAmount());
        repo.save(to);
    }

    @Transactional
    public void transferFailRollbackRuntime(TransferInput transfer) {

        log.info("======== START RUNTIME (ROLLBACK) ========");

        Account from = repo.findById(transfer.getFrom()).orElseThrow();
        Account to = repo.findById(transfer.getTo()).orElseThrow();

        if (from.getBalance() < transfer.getAmount()) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_MONEY);
        }

        from.setBalance(from.getBalance() - transfer.getAmount());
        to.setBalance(to.getBalance() + transfer.getAmount());

        log.info("======== END RUNTIME (SUCCESS) ========");
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferRollbackChecked( TransferInput transfer) throws Exception {

        log.info("======== START TRY-CATCH TRANSACTION ========");

        try {
            Account from = repo.findById(transfer.getFrom()).orElseThrow();
            Account to = repo.findById(transfer.getTo()).orElseThrow();

            from.setBalance(from.getBalance() - transfer.getAmount());
            to.setBalance(to.getBalance() + transfer.getAmount());

            repo.flush();

        } catch (Exception e) {
            throw e;
        }

        log.info("======== END TRY-CATCH TRANSACTION ========");
    }
}