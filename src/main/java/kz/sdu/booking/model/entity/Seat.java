package kz.sdu.booking.model.entity;

import jakarta.persistence.*;
import kz.sdu.booking.model.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.util.List;

@Entity
@Table(name = "t_reservations")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Seat extends AbstractAuditable<Seat, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String number;
	private String location;

	@Enumerated(value = EnumType.STRING)
	private SeatStatus status;

	@OneToMany(mappedBy = "seat")
	private List<Reservation> reservations;
}
