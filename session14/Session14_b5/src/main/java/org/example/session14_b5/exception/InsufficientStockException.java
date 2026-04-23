package org.example.session14_b5.exception;

public class InsufficientStockException extends CheckoutException {

    public InsufficientStockException(String message) {
        super(message);
    }
}

