package kz.sdu.booking.service;

import kz.sdu.booking.Errors;
import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.mapper.UserMapper;
import kz.sdu.booking.model.dto.UserDto;
import kz.sdu.booking.model.dto.UserEditRequestDto;
import kz.sdu.booking.model.entity.User;
import kz.sdu.booking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * Находит пользователя по идентификатору
     * <p/>
     * @param id Идентификатор пользователь
     * @return Информация о пользователе
     */
    public UserDto findUserById(final Long id) throws UserInputException {
        final User user = userRepository.findById(id).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));

        return convertAndFill(user);
    }

    /**
     * Обновляет пользователя по идентификатору
     * <p/>
     * @param id дентификатор пользователя
     * @param requestDto Изменяемые данные
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    public UserDto updateUserById(final Long id, final UserEditRequestDto requestDto) throws UserInputException {
        final User user = userRepository.findById(id).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));

        final String requestFirstName = requestDto.getFirstName();
        if (Objects.nonNull(requestFirstName)) {
            user.setFirstName(requestFirstName);
        }

        final String requestLastName = requestDto.getLastName();
        if (Objects.nonNull(requestLastName)) {
            user.setLastName(requestLastName);
        }

        final String requestPassword = requestDto.getPassword();
        final String password = user.getPassword();
        if (Objects.nonNull(requestPassword)) {
            if (!passwordEncoder.matches(requestPassword, password)) {
                user.setPassword(passwordEncoder.encode(requestPassword));
            }
        }

        userRepository.save(user);

        return convertAndFill(user);
    }

    /**
     * Помечаем пользователя как удаленного
     * <p/>
     * @param userId Идентификатор пользователя
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    public UserDto deleteUser(final Long userId) throws UserInputException {
        final User user = userRepository.findById(userId).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));
        user.setIsDeleted(Boolean.TRUE);

        userRepository.save(user);

        return convertAndFill(user);
    }

    public UserDto convertAndFill(final User user) {
        if (Objects.isNull(user)) {
            return null;
        }

        return UserMapper.INSTANCE.toDto(Optional.of(user));
    }

    public User getAuthenticateUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return (User) authentication.getPrincipal();
    }

}
