package pl.aplazuk.paymentms.service;

    public class NoOrderFoundException extends RuntimeException {
        public NoOrderFoundException(String message) {
            super(message);
        }
    }
