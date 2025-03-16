package kz.sdu.booking.service;

import kz.sdu.booking.mapper.ReservationMapper;
import kz.sdu.booking.model.dto.ReservationDto;
import kz.sdu.booking.model.dto.ReservationRequestDto;
import kz.sdu.booking.model.entity.Reservation;
import kz.sdu.booking.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationDto create(final ReservationRequestDto request) {
        final Reservation reservation = reservationRepository.save(Reservation.newDraft(request));

        return convertAndFill(reservation);
    }

    public ReservationDto convertAndFill(final Reservation reservation) {
        if (Objects.isNull(reservation)) {
            return null;
        }

        return ReservationMapper.INSTANCE.toDto(reservation);
    }
}
