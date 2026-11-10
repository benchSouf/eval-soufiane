package com.eex.application.controller;

import com.eex.application.dto.UpdateAccountNameRequest;
import com.eex.domain.entity.Account;
import com.eex.domain.service.AccountService;
import com.eex.domain.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final JwtUtil jwtUtil;


    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAccountsForAuthenticatedClient() {
        Integer clientId = jwtUtil.getAuthenticatedClientId();
        List<Account> accounts = accountService.getAccountsByClientId(clientId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountByIdForAuthenticatedClient(@PathVariable Integer id) {
        Integer clientId = jwtUtil.getAuthenticatedClientId();
        Optional<Account> account = accountService.getAccountById(id);

        if (account.isPresent() && account.get().getClientId().equals(clientId)) {
            return new ResponseEntity<>(account.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Integer id) {
        Optional<Account> account = accountService.getAccountById(id);
        return account.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Account>> getAccountsByClientId(@PathVariable Integer clientId) {
        List<Account> accounts = accountService.getAccountsByClientId(clientId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Account>> getAccountsByName(@RequestParam String name) {
        List<Account> accounts = accountService.getAccountsByName(name);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Integer id, @RequestBody Account accountDetails) {
        try {
            Account updatedAccount = accountService.updateAccount(id, accountDetails);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        try {
            accountService.deleteAccount(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Account> updateAccountName(
            @PathVariable Integer id,
            @RequestBody UpdateAccountNameRequest updateRequest) {
        Integer clientId = jwtUtil.getAuthenticatedClientId();
        Optional<Account> accountOptional = accountService.getAccountById(id);

        if (accountOptional.isEmpty() || !accountOptional.get().getClientId().equals(clientId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        account.setName(updateRequest.getName());
        Account updatedAccount = accountService.updateAccount(id, account);

        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }
}

