package com.cibertec.denticore.security.controllers;

import com.cibertec.denticore.security.dto.request.LoginRequestDTO;
import com.cibertec.denticore.security.dto.request.RegistroPacienteRequestDTO;
import com.cibertec.denticore.security.dto.response.LoginResponseDTO;
import com.cibertec.denticore.security.services.AuthService;
import com.cibertec.denticore.security.services.JwtService;
import com.cibertec.denticore.security.services.TokenBlackListService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;
    private final TokenBlackListService tokenBlackListService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getDni(),
                        loginRequest.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication);

        String rol = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .orElse("DESCONOCIDO");

        return ResponseEntity.ok(new LoginResponseDTO(token, rol));
    }

    @PostMapping("/registro/paciente")
    public ResponseEntity<String> registrarPaciente(@RequestBody RegistroPacienteRequestDTO dto) {
        authService.registrarPaciente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Paciente registrado con éxito");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");

    tokenBlackListService.invalidarToken(token);

    return ResponseEntity.ok("Sesión cerrada correctamente");
}

}
