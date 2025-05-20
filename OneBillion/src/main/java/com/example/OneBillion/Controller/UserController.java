package com.example.OneBillion.Controller;


import com.example.OneBillion.Model.Customer;
import com.example.OneBillion.Model.OtpUser;
import com.example.OneBillion.Repository.CustomerRepo;
import com.example.OneBillion.Repository.OtpUserRepo;
import com.example.OneBillion.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private OtpUserRepo otpUserRepo;
    @GetMapping("/usr")
    public String user(Model model,@RequestParam(required = false) String email) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails= (UserDetails) authentication.getPrincipal();
        String Username=userDetails.getUsername();
        Customer customer=customerRepo.findByEmail(Username);

        if (email == null && customer.getIsVerified()==false) {
            model.addAttribute("isVerified", false);
        } else {
                model.addAttribute("isVerified", true);
        }
        return "userdashboard";
    }

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/otppage")
    public String showOtpPage(Model model) {
        model.addAttribute("showSendOtpForm", true);
        model.addAttribute("showVerifyOtpForm", false);
        return "otp-page";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, Model model) {
        String response = registrationService.registerOrSendOtp(email);
        model.addAttribute("message", response);
        model.addAttribute("email", email);
        model.addAttribute("showSendOtpForm", false);
        model.addAttribute("showVerifyOtpForm", true);
        return "otp-page";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
        boolean isValid = registrationService.verifyOtp(email, otp);
        if (isValid) {
            return "redirect:/user/usr?email=" + email;
        } else {
            model.addAttribute("error", "Invalid or expired OTP!");
            model.addAttribute("email", email);
            model.addAttribute("showSendOtpForm", false);
            model.addAttribute("showVerifyOtpForm", true);
        }
        return "otp-page";
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, Model model) {
        String message = registrationService.registerOrSendOtp(email);
        model.addAttribute("message", message);
        model.addAttribute("email", email);
        model.addAttribute("showSendOtpForm", false);
        model.addAttribute("showVerifyOtpForm", true);
        return "otp-page";
    }


}