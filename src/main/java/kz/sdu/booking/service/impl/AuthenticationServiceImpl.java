package kz.sdu.booking.service.impl;

import kz.sdu.booking.model.Role;
import kz.sdu.booking.model.User;
import kz.sdu.booking.model.dto.AuthenticationRequest;
import kz.sdu.booking.model.dto.AuthenticationResponse;
import kz.sdu.booking.model.dto.RefreshTokenRequest;
import kz.sdu.booking.model.dto.RegisterRequest;
import kz.sdu.booking.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final kz.sdu.booking.repository.UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final kz.sdu.booking.service.JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(final RegisterRequest registerRequest) {
		final Optional<User> existingUserOptional = userRepository.findByEmail(registerRequest.getEmail());

		if (existingUserOptional.isPresent()) {
			throw new IllegalStateException("User with this email already exists");
		}

		final User user = User.builder()
				.firstName(registerRequest.getFirstName())
				.lastName(registerRequest.getLastName())
				.email(registerRequest.getEmail())
				.password(passwordEncoder.encode(registerRequest.getPassword()))
				.role(Role.ROLE_USER)
				.isExpired(false)
				.isLocked(false)
				.build();

		userRepository.save(user);

		String accessToken = jwtService.generateToken(user);
		String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

		return AuthenticationResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

	}

	@Override
	public AuthenticationResponse authentication(final AuthenticationRequest authenticationRequest) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				authenticationRequest.getEmail(),
				authenticationRequest.getPassword())
		);

		final User user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(
				() -> new UsernameNotFoundException("USER NOT FOUND")
		);

		String accessToken = jwtService.generateToken(user);
		String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

		return AuthenticationResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

	}

	public AuthenticationResponse refreshToken(final RefreshTokenRequest refreshTokenRequest) {
		final String userEmail = jwtService.extractUserEmail(refreshTokenRequest.getRefreshToken());
		final User user = userRepository.findByEmail(userEmail).orElseThrow();
		if (jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), user)) {

			String accessToken = jwtService.generateToken(user);
			String refreshToken = refreshTokenRequest.getRefreshToken();

			return AuthenticationResponse.builder()
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
		}

		return null;
	}
}