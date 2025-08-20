package com.greenheaven.backend.controller;

import com.greenheaven.backend.dto.*;
import com.greenheaven.backend.exception.OldPasswordMismatchException;
import com.greenheaven.backend.repository.MemberRepository;
import com.greenheaven.backend.security.JwtTokenProvider;
import com.greenheaven.backend.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signin")
    public ResponseEntity<TokenDto> signin(@RequestParam("username") String email,
                                           @RequestParam("password") String password) {
        return ResponseEntity.ok(memberService.signIn(email, password));
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth() {
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("로그아웃 성공");
    }

    /**
     * 유저 회원가입
     * @param request 폼의 submit 결과를 담은 dto
     * @return 회원가입 성공 구문
     */
    @PostMapping("/signup")
    public ResponseEntity<String> postSignUp(@RequestBody MemberSignUpDto request) {
        return ResponseEntity.ok(memberService.signUp(request));
    }

    /**
     * 유저 이메일로 찾기
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<String> findMemberByEmail(@PathVariable("email") String memberEmail) {
        if (memberService.checkEmail(memberEmail).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 이메일을 찾을 수 없습니다.");
        } else {
            return ResponseEntity.ok("해당 이메일을 찾았습니다.");
        }
    }

    /**
     * 유저 비밀번호 재설정
     */
    @PostMapping("/password-reset/{email}")
    public ResponseEntity<String> postFindPwdResult(@PathVariable("email") String memberEmail) {
            return ResponseEntity.ok(memberService.sendPasswordByEmail(memberEmail));
    }


    /**
     * 유저 프로필 조회
     */
    @GetMapping("/profile")
    public ResponseEntity<MemberProfileResponseDto> getProfile() {
        return ResponseEntity.ok(memberService.getProfile());
    }

    /**
     * 유저 프로필 수정
     */
    @PostMapping("/profile")
    public void postFindPwdResult(@RequestBody MemberProfileRequestDto request) throws OldPasswordMismatchException {
        memberService.updateProfile(request);
    }

    /**
     * 유저 탈퇴
     */
    @PostMapping("/quit")
    public void quit() {
        memberService.quit();
    }


}
