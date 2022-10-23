package com.saeed.spring.security.client.service;

import com.saeed.spring.security.client.entity.PasswordReseToken;
import com.saeed.spring.security.client.entity.User;
import com.saeed.spring.security.client.entity.VerificationToken;
import com.saeed.spring.security.client.model.UserModel;
import com.saeed.spring.security.client.repository.PasswordResetTokenRepository;
import com.saeed.spring.security.client.repository.UserRespository;
import com.saeed.spring.security.client.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRespository userRespository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole("User");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));

        userRespository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken =
                new VerificationToken(user, token);

        verificationTokenRepository.save(verificationToken);

    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "invalid";
        }

        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if (verificationToken.getExpirationTime().getTime()
                - calendar.getTime().getTime() <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }

        user.setEnabled(true);
        userRespository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRespository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordReseToken passwordResetToken =
                new PasswordReseToken(user,token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordReseToken passwordReseToken =
                passwordResetTokenRepository.findByToken(token);
        if (passwordReseToken == null) {
            return "invalid";
        }

        User user = passwordReseToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if (passwordReseToken.getExpirationTime().getTime()
                - calendar.getTime().getTime() <= 0) {
            passwordResetTokenRepository.delete(passwordReseToken);
            return "expired";
        }

        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRespository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
        
    }
}
