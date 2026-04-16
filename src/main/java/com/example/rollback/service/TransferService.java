package com.example.rollback.service;

import com.example.rollback.entity.Account;
import com.example.rollback.exception.CustomException;
import com.example.rollback.exception.ErrorCode;
import com.example.rollback.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        repo.save(from);
        repo.save(to);

        log.info("======== END SUCCESS ========");
        return "SUCCESS";
    }

    @Transactional
    public void transferSuccessButCheckedException(String fromId, String toId, double amount) throws Exception {

        log.info("======== START CHECKED (NO ROLLBACK) ========");

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        repo.save(from);
        repo.save(to);

        throw new CustomException(ErrorCode.CHECKED_EXCEPTION);
    }

    @Transactional
    public void transferFailRollbackRuntime(String fromId, String toId, double amount) {

        log.info("======== START RUNTIME (ROLLBACK) ========");

        Account from = repo.findById(fromId).orElseThrow();

        if (from.getBalance() < amount) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_MONEY);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferRollbackChecked(String fromId, String toId, double amount) throws Exception {

        log.info("======== START CHECKED (ROLLBACK) ========");

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        repo.save(from);
        repo.save(to);

        throw new CustomException(ErrorCode.CHECKED_ROLLBACK);
    }
}