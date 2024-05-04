package org.golive.account.interfaces;

public interface IAccountTokenRPC {

    String createAndSaveLoginToken(Long userId);

    Long getUserIdByToken(String tokenKey);
}
