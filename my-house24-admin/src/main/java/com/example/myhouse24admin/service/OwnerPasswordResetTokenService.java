package com.example.myhouse24admin.service;


public interface OwnerPasswordResetTokenService {
    String createOrUpdatePasswordResetToken(Long ownerId);

}
