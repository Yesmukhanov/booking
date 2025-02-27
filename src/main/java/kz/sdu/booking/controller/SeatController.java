package kz.sdu.booking.controller;

import kz.sdu.booking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seats")
public class SeatController {

	private final SeatService seatService;
}
