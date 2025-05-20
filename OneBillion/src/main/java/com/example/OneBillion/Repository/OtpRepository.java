package com.example.OneBillion.Repository;


import com.example.OneBillion.Model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByEmail(String email);

    void deleteByEmail(String email);
}

