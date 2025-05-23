package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.dto.MemberFindPwdDto;
import com.greenheaven.greenheaven_app.dto.MemberProfileDto;
import com.greenheaven.greenheaven_app.dto.MemberSignUpDto;
import com.greenheaven.greenheaven_app.exception.*;
import com.greenheaven.greenheaven_app.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 유저 로그인 GET
     * @return 로그인 페이지
     */
    @GetMapping("/login")
    public String getLogin() {
        // 로그인 페이지로 이동
        return "login";
    }

    /**
     * 유저 회원가입 GET
     * @param model 뷰에 데이터를 전달하가 위한 개체
     * @return 회원가입 페이지
     */
    @GetMapping("/signup")
    public String getSignUp(Model model) {
        // 모델에 회원가입 폼을 담을 DTO를 추가한 뒤, 회원가입 페이지로 이동
        model.addAttribute("memberSignUpDto", MemberSignUpDto.builder().build());
        return "signup";
    }


    /**
     * 유저 회원가입 POST
     * @param request 폼의 submit 결과를 담은 dto
     * @param bindingResult 사용자 입력을 검증
     * @param model 뷰에 데이터를 전달하가 위한 개체
     * @return 로그인 페이지로 리다이렉트
     */
    @PostMapping("/signup")
    public String postSignUp(@ModelAttribute("memberSignUpDto") @Validated MemberSignUpDto request,
                             BindingResult bindingResult, Model model) throws ParseException {
        // 사용자 입력 검증 중 오류 발생 시, 각 필드에 해당하는 에러 메시지를 모델에 추가 한 뒤 기존 페이지로 돌아간다
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> { 
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            return "signup";
        }

        // 회원가입 로직 수행, 각 필드에 대한 추가 검증 중 예외 발생 시 메시지를 모델에 추가 한 뒤 기존 페이지로 돌아간다
        try {
            memberService.SignUp(request);
        } catch (EmailExistException ex) {
            model.addAttribute("emailError", ex.getMessage());
            return "signup";
        } catch (OldPasswordMismatchException ex) {
            model.addAttribute("confirmPasswordError", ex.getMessage());
            return "signup";
        }

        // 로직 성공을 알리는 쿼리 파라미터와 함께 로그인 페이지로 리다이렉트
        return "redirect:/member/login?signup=true";
    }

    /**
     *  유저 비밀번호 찾기 GET
     * @param model 뷰에 데이터를 전달하가 위한 개체
     * @return 비밀번호 찾기 페이지
     */
    @GetMapping("/password/reset")
    public String getFindPwd(Model model) {
        // 모델에 비밀번호 찾기 폼을 담을 DTO를 추가한 뒤, 비밀번호 찾기 페이지로 이동
        model.addAttribute("memberFindPwdDto", new MemberFindPwdDto());
        return "findpwd";
    }

    /**
     * 유저 비밀번호 찾기 POST
     * @param request 폼의 submit 결과를 담은 dto
     * @param bindingResult 사용자 입력을 검증
     * @param model 뷰에 데이터를 전달하가 위한 개체
     * @param redirectAttributes 리다이렉트할 페이지에 데이터를 전달하기 위한 객체
     * @return 비밀번호 찾기결과 페이지로 리다이렉트
     */
    @PostMapping("/password/reset")
    public String postFindPwd(@ModelAttribute("memberFindPwdDto") @Validated MemberFindPwdDto request,
                       BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // 사용자 입력 검증 중 오류 발생 시, 각 필드에 해당하는 에러 메시지를 모델에 추가 한 뒤 기존 페이지로 돌아간다
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            return "findpwd";
        }

        // 이메일로 유저를 찾지 못하면, 에러 메시지를 모델에 추가 한 뒤 기존 페이지로 돌아간다
        if (memberService.checkEmail(request.getEmail()).isEmpty()) {
            model.addAttribute("checkEmailError", "해당 이메일을 찾을 수 없습니다."); // 모델에 에러 담기
            return "findpwd";
        } else { // 이메일로 유저를 찾으면, 리다이렉트할 페이지에 표시할 유저 이메일을 redirectAttributes에 추가한 뒤 리다이렉트
            redirectAttributes.addFlashAttribute("memberEmail", request.getEmail());
            return "redirect:/member/password/reset/confirm";
        }
    }

    /**
     * 유저 비밀번호 재설정 GET
     * @param model 뷰에 데이터를 전달하가 위한 개체
     * @return 비밀번호 재설정 페이지
     */
    @GetMapping("/password/reset/confirm")
    public String getFindPwdResult(Model model) {
        // 모델에 비밀번호 재설정 폼을 담을 DTO를 추가한 뒤, 비밀번호 재설정 페이지로 이동
        model.addAttribute("memberFindPwdDto", new MemberFindPwdDto());
        return "findpwdresult";
    }

    /**
     * 유저 비밀번호 재설정 POST
     * @param memberEmail 유저의 이메일
     * @return 로그인 페이지로 리다이렉트
     */
    @PostMapping("/password/reset/confirm")
    public String postFindPwdResult(@RequestParam("memberEmail") String memberEmail) {
        // 비밀번호 재설정을 위한 메일 전송 로직 수행 후, 성공을 알리는 쿼리 파라미터와 함께 리다이렉트
        memberService.sendPasswordByEmail(memberEmail);
        return "redirect:/member/login?findpwd=true";
    }


    /**
     * 유저 프로필 GET
     * @param model 뷰에 데이터를 전달하가 위한 개체
     * @return 프로필 페이지
     */
    @GetMapping("/profile")
    public String getProfile(Model model) {
        // 유저 프로필 조회 로직 수행 후, 수행 결과를 담은 DTO를 모델에 추가한다
        MemberProfileDto memberProfileDto = memberService.getProfile();
        model.addAttribute("memberProfileDto", memberProfileDto);

        // 프로필 페이지로 이동
        return "profile";
    }

    /**
     * 유저 프로필 POST
     * @param request 폼의 submit 결과를 담은 dto
     * @param bindingResult 사용자 입력을 검증
     * @param model 뷰에 데이터를 전달하가 위한 개체
     * @return 메인 페이지로 리다이렉트
     */
    @PostMapping("/profile")
    public String postFindPwdResult(@ModelAttribute("memberProfileDto") @Validated MemberProfileDto request,
                                    BindingResult bindingResult, Model model) {
        // 사용자 입력 검증 중 오류 발생 시, 각 필드에 해당하는 에러 메시지를 모델에 추가 한 뒤 기존 페이지로 돌아간다
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            return "profile";
        }

        // 프로필 업데이트 로직 수행, 각 필드에 대한 추가 검증 중 예외 발생 시 메시지를 모델에 추가 한 뒤 기존 페이지로 돌아간다
        try {
            memberService.updateProfile(request);
        } catch (OldPasswordMismatchException ex) {
            model.addAttribute("oldPasswordError", ex.getMessage());
            return "profile";
        } catch (NewPasswordLengthException ex) {
            model.addAttribute("newPasswordError", ex.getMessage());
            return "profile";
        } catch (NewPasswordBlankException | NewPasswordMismatchException ex) {
            model.addAttribute("confirmNewPasswordError", ex.getMessage());
            return "profile";
        }

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    /**
     * 유저 탈퇴 GET
     * @param model 뷰에 데이터를 전달하가 위한 개체
     * @return 탈퇴 페이지
     */
    @GetMapping("/quit")
    public String getQuit(Model model) {
        // 탈퇴 페이지로 이동
        return "quit";
    }

    /**
     * 유저 탈퇴 POST
     * @param request 클라이언트의 HTTP 요청을 담은 객체
     * @param response 서버의 HTTP 응답을 담은 객체
     * @return 홈 페이지로 리다이렉트
     */
    @PostMapping("/quit")
    public String postQuit(HttpServletRequest request, HttpServletResponse response) {
        // 유저 탈퇴 처리
        memberService.quit(request, response);

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }


}
