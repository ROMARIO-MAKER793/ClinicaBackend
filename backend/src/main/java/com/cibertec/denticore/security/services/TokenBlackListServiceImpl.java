package com.cibertec.denticore.security.services;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlackListServiceImpl implements TokenBlackListService {

    private final Set<String> tokensInvalidos = ConcurrentHashMap.newKeySet();

    @Override
    public void invalidarToken(String token) {
        tokensInvalidos.add(token);
    }

    @Override
    public boolean estaInvalidado(String token) {
        return tokensInvalidos.contains(token);
    }
}