package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.domain.dto.UserLoginDto;
import com.greenheaven.greenheaven_app.domain.dto.UserSignUpDto;
import com.greenheaven.greenheaven_app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/signup")
    public String getSignUp(Model model) {
        model.addAttribute("userSignUpDto", new UserSignUpDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String postSignUp(@ModelAttribute("userSignUpDto") @Validated UserSignUpDto request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) { // validate 유효성 검증에서 오류 발생 시
            bindingResult.getFieldErrors().forEach(error -> { //에러 담기
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            return "signup";
        }
        if (userService.checkEmail(request).isPresent()) { // 이미 가입된 이메일이면
            model.addAttribute("checkEmailError", "이미 가입된 이메일입니다."); // 모델에 에러 담기
            return "signup";
        }

        if (!request.getConfirmPassword().equals(request.getPassword())) { // 비밀번호 확인과 비밀번호에 입력된 값이 다르면
            model.addAttribute("pwdEqualError", "비밀번호와 동일해야 합니다."); // 모델에 에러 담기
            return "signup";
        }

        userService.SignUp(request); // 오류 검증 로직 모두 통과 시, 회원가입
        return "redirect:/user/login?signup=true"; // 로그인 페이지로 이동시키기
    }
}
