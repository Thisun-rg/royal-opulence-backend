package com.royalopulence.hotelmanagement.service;

import com.royalopulence.hotelmanagement.dto.*;
public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}