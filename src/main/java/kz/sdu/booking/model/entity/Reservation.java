package kz.sdu.booking.model.entity;

import jakarta.persistence.*;
import kz.sdu.booking.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_reservations")
@Builder
@Getter
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
}
