package kz.sdu.booking.controller;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.ReservationDto;
import kz.sdu.booking.model.dto.ReservationRequestDto;
import kz.sdu.booking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rest/sdu/booking/reservations", produces = APPLICATION_JSON_VALUE)
public class ReservationController {
	private final ReservationService reservationService;

	/**
	 * Создает бронирование места для пользователя
	 * <p/>
	 * @param request объект {@link ReservationRequestDto}, содержащий данные для бронирования
	 * @return объект {@link ReservationDto}, содержащий информацию о созданном бронировании
	 * @throws UserInputException если переданы некорректные данные (например, место уже занято или время указано неверно).
	 */
	@PostMapping("/create")
	public ReservationDto createReservation(@RequestBody final ReservationRequestDto request) throws UserInputException {
		return reservationService.create(request);
	}

}
