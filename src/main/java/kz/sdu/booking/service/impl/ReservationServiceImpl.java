package kz.sdu.booking.service.impl;

import kz.sdu.booking.repository.ReservationRepository;
import kz.sdu.booking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;
}
