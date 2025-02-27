package kz.sdu.booking.mapper;

import kz.sdu.booking.model.dto.ReservationDTO;
import kz.sdu.booking.model.entity.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

	ReservationDTO toDto(Reservation reservation);
}
