package kz.sdu.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.annotation.Nullable;
import kz.sdu.booking.handle.UserInputException;

public class ThrowIf {

    public static void isTrue(
        @Nullable final Boolean obj,
        @NotNull final String msg
    ) throws UserInputException {
        if (Boolean.TRUE.equals(obj)) {
            throw new UserInputException(msg);
        }
    }
}
