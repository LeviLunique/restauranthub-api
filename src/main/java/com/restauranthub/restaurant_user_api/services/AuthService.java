package com.restauranthub.restaurant_user_api.services;

import com.restauranthub.restaurant_user_api.dto.LoginRequest;
import com.restauranthub.restaurant_user_api.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
