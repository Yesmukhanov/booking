package kz.sdu.booking.service;

import kz.sdu.booking.utils.Errors;
import kz.sdu.booking.utils.ThrowIf;
import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.mapper.ReservationMapper;
import kz.sdu.booking.model.dto.ReservationDto;
import kz.sdu.booking.model.dto.ReservationRequestDto;
import kz.sdu.booking.model.entity.Reservation;
import kz.sdu.booking.model.entity.Seat;
import kz.sdu.booking.model.entity.User;
import kz.sdu.booking.model.enums.ReservationStatus;
import kz.sdu.booking.model.enums.Role;
import kz.sdu.booking.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final SeatService seatService;

    /**
     *
     * @param request
     * @return
     * @throws UserInputException
     */
    public ReservationDto create(final ReservationRequestDto request) throws UserInputException {
        final User user = userService.getAuthenticateUser();
        if (user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.LIBRARIAN)) {
            return new ReservationDto();
        }

        final Seat seat = seatService.findById(request.getSeatId());
        ThrowIf.isTrue(reservationRepository.existsBySeatAndTime(seat, request.getStartTime(), request.getEndTime()),
                       Errors.MSG_SEAT_ALREADY_BOOKED);

        final Reservation reservation = reservationRepository.save(Reservation.newDraft(request, user, seat));

        return convertAndFill(reservation);
    }

    /**
     * Получает список бронирований по идентификатору пользователя или места.
     * <p/>
     * @param userId идентификатор пользователя
     * @param seatId идентификатор места
     * @return список объектов {@link ReservationDto}, представляющих бронирования
     */
    public List<ReservationDto> getReservationList(final Long userId, final Long seatId) {
        if (Objects.isNull(userId) && Objects.isNull(seatId)) {
            return Collections.emptyList();
        }

        final List<Reservation> reservations = (Objects.nonNull(userId))
                                               ? reservationRepository.findByUserId(userId)
                                               : reservationRepository.findBySeatId(seatId);

        return convertAndFillList(reservations);
    }

    /**
     * Получает детали бронирования по его идентификатору
     * <p/>
     * @param id идентификатор бронирования
     * @return объект {@link ReservationDto}, содержащий информацию о бронировании
     * @throws UserInputException если бронирование с указанным ID не найдено
     */
    public ReservationDto getReservationById(Long id) throws UserInputException {
        final Reservation reservation = reservationRepository.findById(id)
                                                       .orElseThrow(() -> new UserInputException(String.format(Errors.MSG_RESERVATION_NOT_FOUND, id)));

        return convertAndFill(reservation);
    }


    /**
     * Конвертирует entity в dto
     * @param reservation объект резерва
     * @return Об
     */
    public ReservationDto convertAndFill(final Reservation reservation) {
        if (Objects.isNull(reservation)) {
            return null;
        }

        return ReservationMapper.INSTANCE.toDto(reservation);
    }

    public List<ReservationDto> convertAndFillList(final List<Reservation> reservationList) {
        if (CollectionUtils.isEmpty(reservationList)) {
            return null;
        }

        return ReservationMapper.INSTANCE.toDtoList(reservationList);
    }

    /**
     * Отменяет бронирование по его идентификатору (только для ADMIN)
     * <p>
     * Отмена доступна только пользователям с ролью {@code LIBRARIAN} или {@code ADMIN}.
     * Если пользователь не имеет достаточных прав или бронирование не найдено, выбрасывается исключение.
     * <p/>
     * @param id идентификатор бронирования, которое необходимо отменить
     * @return объект {@link ReservationDto}, представляющий обновленное бронирование со статусом {@code CANCELLED}
     * @throws UserInputException если бронирование не найдено или у пользователя недостаточно прав
     */
    public ReservationDto cancelReservation(final Long id) throws UserInputException {
        // Получаем аутентифицированного пользователя
        final User user = userService.getAuthenticateUser();

        // Проверяем, является ли пользователь LIBRARIAN или ADMIN
        if (!(user.getRole().equals(Role.LIBRARIAN) || user.getRole().equals(Role.ADMIN))) {
            throw new UserInputException(Errors.MSG_ACCESS_DENIED);
        }

        // Ищем бронирование в базе
        final Reservation reservation =
            reservationRepository.findById(id)
                                 .orElseThrow(() -> new UserInputException(String.format(Errors.MSG_RESERVATION_NOT_FOUND, id)));

        // Меняем статус на "ОТМЕНЕНО"
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return convertAndFill(reservation);
    }

}
