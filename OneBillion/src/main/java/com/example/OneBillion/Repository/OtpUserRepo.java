package com.example.OneBillion.Repository;


import com.example.OneBillion.Model.OtpUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpUserRepo extends JpaRepository<OtpUser, Long> {
    Optional<OtpUser> findByEmail(String email);
}
