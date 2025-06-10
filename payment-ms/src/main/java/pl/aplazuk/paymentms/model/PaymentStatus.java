package pl.aplazuk.paymentms.model;

import java.util.Arrays;
import java.util.Random;

public enum PaymentStatus {
    OPEN, ERROR, IN_PROGRESS, COMPLETED, CANCELLED;

    private static final int SIZE = PaymentStatus.values().length;
    private static final Random RANDOM = new Random();

    public static PaymentStatus getRandomStatus() {
       return Arrays.stream(PaymentStatus.values()).toList().get(RANDOM.nextInt(SIZE));
    }
}
