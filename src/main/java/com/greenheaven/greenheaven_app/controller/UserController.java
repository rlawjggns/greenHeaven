package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.domain.dto.UserFindPwdDto;
import com.greenheaven.greenheaven_app.domain.dto.UserProfileDto;
import com.greenheaven.greenheaven_app.domain.dto.UserSignUpDto;
import com.greenheaven.greenheaven_app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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
        if (userService.checkEmail(request.getEmail()).isPresent()) { // 이미 가입된 이메일이면
            model.addAttribute("checkEmailError", "이미 가입된 이메일입니다."); // 모델에 에러 담기
            return "signup";
        }

        if (!request.getConfirmPassword().equals(request.getPassword())) { // 비밀번호 확인과 비밀번호에 입력된 값이 다르면
            model.addAttribute("pwdEqualError", "비밀번호가 일치하지 않습니다."); // 모델에 에러 담기
            return "signup";
        }

        userService.SignUp(request); // 오류 검증 로직 모두 통과 시, 회원가입
        return "redirect:/user/login?signup=true"; // 로그인 페이지로 이동시키기
    }

    @GetMapping("/password/reset")
    public String getFindPwd(Model model) {
        model.addAttribute("userFindPwdDto", new UserFindPwdDto());
        return "findpwd";
    }

    @PostMapping("/password/reset")
    public String post(@ModelAttribute("userFindPwdDto") @Validated UserFindPwdDto request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> { // 에러 담기
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            return "findpwd";
        }

        if (userService.checkEmail(request.getEmail()).isEmpty()) { // 이메일로 유저를 찾지 못하면
            model.addAttribute("checkEmailError", "해당 이메일을 찾을 수 없습니다."); // 모델에 에러 담기
            return "findpwd";
        } else { // 이메일로 유저를 찾았다면
            redirectAttributes.addFlashAttribute("userEmail", request.getEmail());
            return "redirect:/user/password/reset/confirm";
        }
    }

    @GetMapping("/password/reset/confirm")
    public String getFindPwdResult(Model model) {
        model.addAttribute("userFindPwdDto", new UserFindPwdDto());
        return "findpwdresult";
    }

    @PostMapping("/password/reset/confirm")
    public String postFindPwdResult(@RequestParam("userEmail") String userEmail) {
        userService.sendPasswordByEmail(userEmail);
        return "redirect:/user/login?findpwd=true";
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        UserProfileDto userProfileDto = userService.getUserProfile(email);

        model.addAttribute("userProfileDto", userProfileDto);
        return "profile";
    }

    @PostMapping("/profile")
    public String postFindPwdResult(@ModelAttribute("userProfileDto") @Validated UserProfileDto request, BindingResult bindingResult, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();


        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> { // 에러 담기
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            return "profile";
        }

        if(!passwordEncoder.matches(request.getOldPassword(), userService.findPwd(email))) { // 비밀번호와 일치하지 않을경우
            model.addAttribute("pwdEqualError", "기본 비밀번호가 일치하지 않습니다."); // 모델에 에러 담기
            return "profile";
        }

        if (!request.getNewPassword().isBlank() &&
                (request.getNewPassword().length() < 8 || request.getNewPassword().length() > 20)) { // 새로운 비밀번호는 입력되었지만, 최소 최대 자리수를 만족하지 않을 경우
            model.addAttribute("newPasswordError", "최소 8글자 이상, 최대 20자 이하"); // 모델에 에러 담기
            return "profile";
        }


        if(!request.getNewPassword().isBlank() && request.getConfirmNewPassword().isBlank()) { // 새로운 비밀번호는 입력되었지만, 새로운 비밀번호 확인이 비어있을 경우
            model.addAttribute("newPwdBlankError", "비밀번호를 한번 더 입력하세요."); // 모델에 에러 담기
            return "profile";
        }

        if (!request.getNewPassword().isBlank() && !request.getNewPassword().equals(request.getConfirmNewPassword())) { // 새로운 비밀번호는 입력되었지만, 새로운 비밀번호 확인과 일치하지 않으면
            model.addAttribute("newPwdEqualError", "비밀번호가 일치하지 않습니다."); // 모델에 에러 담기
            return "profile";
        }

        userService.updateProfile(request, email);
        return "redirect:/";
    }

    @GetMapping("/quit")
    public String getQuit(Model model) {
        return "quit";
    }

    @PostMapping("/quit")
    public String postQuit(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        userService.quit(email);

        // 🔹 세션 무효화 및 로그아웃 처리
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/"); // 애플리케이션 전체에 적용된 쿠키 삭제
        response.addCookie(cookie);

        return "redirect:/";
    }
}
