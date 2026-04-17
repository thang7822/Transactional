package com.example.rollback.service;

import com.example.rollback.entity.Account;
import com.example.rollback.exception.CustomException;
import com.example.rollback.exception.ErrorCode;
import com.example.rollback.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository repo;

    @Transactional
    public String transferSuccess(String fromId, String toId, double amount) {

        log.info("======== START SUCCESS ========");

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        log.info("======== END SUCCESS ========");
        return "SUCCESS";
    }

    public String transferWithoutTransaction(String fromId, String toId, double amount) throws SQLException {
        log.info("======== START NO TRANSACTION ========");

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        from.setBalance(from.getBalance() - amount);
        repo.save(from);

        boolean isDatabaseError = true;
        if (isDatabaseError) {
            throw new CustomException(ErrorCode.SQLException);
        }

        to.setBalance(to.getBalance() + amount);
        repo.save(to);

        log.info("======== END NO TRANSACTION ========");
        return "transfer Without Transaction";
    }

    @Transactional
    public void transferSuccessButCheckedException(String fromId, String toId, double amount) throws Exception {

        log.info("======== START TRANSACTION (WRONG CONFIG) ========");

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        from.setBalance(from.getBalance() - amount);

        boolean isDatabaseError = true;
        if (isDatabaseError) {
            throw new CustomException(ErrorCode.SQLException);
        }

        to.setBalance(to.getBalance() + amount);
    }

    @Transactional
    public void transferFailRollbackRuntime(String fromId, String toId, double amount) {

        log.info("======== START RUNTIME (ROLLBACK) ========");

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        if (from.getBalance() < amount) {

            throw new CustomException(ErrorCode.NOT_ENOUGH_MONEY);
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        log.info("======== END RUNTIME (SUCCESS) ========");
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferRollbackChecked(String fromId, String toId, double amount) throws Exception {

        log.info("======== START TRY-CATCH TRANSACTION ========");

        try {
            Account from = repo.findById(fromId).orElseThrow();
            Account to = repo.findById(toId).orElseThrow();

            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);

            repo.flush();

        } catch (CustomException e) {
            e.getMessage();
            throw e;
        }

        log.info("======== END TRY-CATCH TRANSACTION ========");
    }
}