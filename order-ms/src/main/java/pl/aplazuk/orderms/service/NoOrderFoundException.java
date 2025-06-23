package pl.aplazuk.orderms.service;

public class NoOrderFoundException extends RuntimeException {

    private final String responseBody;

    public NoOrderFoundException(String message, String responseBody) {
        super(message);
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
