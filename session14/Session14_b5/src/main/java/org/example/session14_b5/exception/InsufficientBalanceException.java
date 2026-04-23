package org.example.session14_b5.exception;

public class InsufficientBalanceException extends CheckoutException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}

