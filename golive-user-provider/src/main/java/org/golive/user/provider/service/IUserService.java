package org.golive.user.provider.service;

import org.golive.user.dto.UserDTO;

public interface IUserService {
    UserDTO getByUserId(Long UserId);
}
