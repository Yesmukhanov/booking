package kz.sdu.booking.model.entity;

import jakarta.persistence.*;
import kz.sdu.booking.model.dto.ReservationRequestDto;
import kz.sdu.booking.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_reservations")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation extends AbstractAuditable<Reservation, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "seat_id")
	private Seat seat;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	private ReservationStatus status;

	public static Reservation newDraft(final ReservationRequestDto request) {
		final Reservation reservation = new Reservation();
		reservation.setStartTime(request.getStartTime());
		reservation.setEndTime(request.getEndTime());

		return reservation;
	}
}
