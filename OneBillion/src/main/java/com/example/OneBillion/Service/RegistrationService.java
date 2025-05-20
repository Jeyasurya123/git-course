package com.example.OneBillion.Service;

import com.example.OneBillion.Model.Customer;
import com.example.OneBillion.Model.OtpUser;
import com.example.OneBillion.Model.OtpVerification;
import com.example.OneBillion.Repository.CustomerRepo;
import com.example.OneBillion.Repository.OtpRepository;
import com.example.OneBillion.Repository.OtpUserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OtpUserRepo otpUserRepo;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_VALIDITY_DURATION = 30;

    @Transactional
    public String registerOrSendOtp(String email) {
        Optional<OtpUser> existingUser = otpUserRepo.findByEmail(email);

        if (existingUser.isEmpty()) {
            OtpUser newUser = new OtpUser();
            newUser.setEmail(email);
            otpUserRepo.save(newUser);
        }

        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(OTP_VALIDITY_DURATION);

        otpRepository.deleteByEmail(email);

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setExpiryTime(expiryTime);
        otpRepository.save(otpVerification);

        emailService.sendOtp(email, otp);

        return "OTP sent to email: " + email;
    }

    @Transactional
    public boolean verifyOtp(String email, String otp) {
        Optional<OtpVerification> otpDetailsOpt = otpRepository.findByEmail(email);
        Customer customer=customerRepo.findByEmail(email);

        if (otpDetailsOpt.isEmpty() || LocalDateTime.now().isAfter(otpDetailsOpt.get().getExpiryTime())) {
            otpRepository.deleteByEmail(email);
            return false;
        }

        if (otpDetailsOpt.get().getOtp().equals(otp)) {
            otpRepository.deleteByEmail(email);
            Optional<OtpUser> userOpt = otpUserRepo.findByEmail(email);
            userOpt.ifPresent(user -> {
                user.setVerified(true);
                customer.setVerified(true);
                otpUserRepo.save(user);
            });
            return true;
        }

        return false;
    }
    @Transactional
    public void deleteExistingOtp(String email) {
        otpRepository.deleteByEmail(email);
    }
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }

}
