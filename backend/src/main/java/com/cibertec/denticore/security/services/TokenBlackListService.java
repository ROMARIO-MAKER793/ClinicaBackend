package com.cibertec.denticore.security.services;

public interface TokenBlackListService {

    void invalidarToken(String token);

    boolean estaInvalidado(String token);
}