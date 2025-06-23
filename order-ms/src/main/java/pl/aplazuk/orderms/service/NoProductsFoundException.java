package pl.aplazuk.orderms.service;

public class NoProductsFoundException extends RuntimeException {

    private final String responseBody;

    public NoProductsFoundException(String message, String responseBody) {
        super(message);
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
