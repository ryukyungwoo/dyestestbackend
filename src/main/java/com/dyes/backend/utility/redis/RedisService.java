package com.dyes.backend.utility.redis;

import com.dyes.backend.domain.user.entity.User;

public interface RedisService {
    void setUUIDAndUser(String UUID, String userId);
}
