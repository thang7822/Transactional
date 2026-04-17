package com.example.rollback.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    CHECKED_EXCEPTION(101,"checked exception commit"),
    NOT_ENOUGH_MONEY(102,"Not enough money"),
    CHECKED_ROLLBACK(103,"Checked but rollback"),
    SQLException(104,"Connection lost or SQL constraint violated!"),
    ;

    int code;
    String message;
}
