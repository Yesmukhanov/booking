package kz.sdu.booking.model.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationRequestDto {
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
