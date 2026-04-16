package com.example.rollback.service;

import com.example.rollback.entity.Account;
import com.example.rollback.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository repo;

    // ================= SUCCESS =================
    @Transactional
    public void transferSuccess(String fromId, String toId, double amount) {

        log.info("======== START SUCCESS ========");
        log.info("Transaction active: {}", TransactionSynchronizationManager.isActualTransactionActive());

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        log.info("Before | from={} | to={}", from.getBalance(), to.getBalance());

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        repo.save(from);
        repo.save(to);

        log.info("After  | from={} | to={}", from.getBalance(), to.getBalance());
        log.info("======== END SUCCESS ========");
    }

    // ================= CHECKED (NO ROLLBACK) =================
    @Transactional
    public void transferSuccessButCheckedException(String fromId, String toId, double amount) throws Exception {

        log.info("======== START CHECKED (NO ROLLBACK) ========");

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        log.info("Before | from={} | to={}", from.getBalance(), to.getBalance());

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        repo.save(from);
        repo.save(to);

        log.info("After  | from={} | to={}", from.getBalance(), to.getBalance());
        log.warn("⚠️ Throw CHECKED EXCEPTION → sẽ COMMIT");

        throw new Exception("Checked Exception");
    }

    // ================= RUNTIME (ROLLBACK) =================
    @Transactional
    public void transferFailRollbackRuntime(String fromId, String toId, double amount) {

        log.info("======== START RUNTIME (ROLLBACK) ========");

        Account from = repo.findById(fromId).orElseThrow();

        log.info("Balance hiện tại: {}", from.getBalance());

        if (from.getBalance() < amount) {
            log.error("❌ Không đủ tiền → THROW RuntimeException → ROLLBACK");
            throw new RuntimeException("Not enough money");
        }
    }

    // ================= CHECKED + ROLLBACK =================
    @Transactional(rollbackFor = Exception.class)
    public void transferRollbackChecked(String fromId, String toId, double amount) throws Exception {

        log.info("======== START CHECKED (ROLLBACK) ========");

        Account from = repo.findById(fromId).orElseThrow();
        Account to = repo.findById(toId).orElseThrow();

        log.info("Before | from={} | to={}", from.getBalance(), to.getBalance());

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        repo.save(from);
        repo.save(to);

        log.info("After  | from={} | to={}", from.getBalance(), to.getBalance());
        log.warn("⚠️ Throw CHECKED EXCEPTION → sẽ ROLLBACK");

        throw new Exception("Checked nhưng rollback");
    }
}