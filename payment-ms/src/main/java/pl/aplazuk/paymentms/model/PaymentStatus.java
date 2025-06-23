package pl.aplazuk.paymentms.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.Random;

@JsonFormat(shape = JsonFormat.Shape.STRING) //enum serialized as a string (enum order avoid)
public enum PaymentStatus {
    OPEN, ERROR, IN_PROGRESS, COMPLETED, CANCELLED;

    private static final int SIZE = PaymentStatus.values().length;
    private static final Random RANDOM = new Random();

    public static PaymentStatus getRandomStatus() {
       return Arrays.stream(PaymentStatus.values()).toList().get(RANDOM.nextInt(SIZE));
    }
}
