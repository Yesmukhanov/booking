package kz.sdu.booking.model.dto;

import kz.sdu.booking.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

	private Integer id;
	private UserDTO user;
	private SeatDTO seat;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private ReservationStatus status;
}
