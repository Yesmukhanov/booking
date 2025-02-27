package kz.sdu.booking.service.impl;

import kz.sdu.booking.repository.SeatRepository;
import kz.sdu.booking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

	private final SeatRepository seatRepository;
}
