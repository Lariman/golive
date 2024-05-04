package org.golive.account.provider.service;

public interface IAccountTokenService {
    String createAndSaveLoginToken(Long userId);

    Long getUserIdByToken(String tokenKey);
}
