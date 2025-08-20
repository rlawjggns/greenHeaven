package com.greenheaven.backend.service;

import com.greenheaven.backend.domain.MemberRole;
import com.greenheaven.backend.dto.MemberProfileRequestDto;
import com.greenheaven.backend.dto.MemberProfileResponseDto;
import com.greenheaven.backend.dto.MemberSignUpDto;
import com.greenheaven.backend.domain.Member;
import com.greenheaven.backend.dto.TokenDto;
import com.greenheaven.backend.exception.*;
import com.greenheaven.backend.repository.MemberRepository;
import com.greenheaven.backend.security.JwtTokenProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.key}")
    private String kakaoKey;

    /**
     * 로그인
     */
    public TokenDto signIn(String email, String password) {
        return memberRepository.findByEmail(email)
                .filter(member -> passwordEncoder.matches(password, member.getPassword()))
                .map(member -> {
                    String accessToken = jwtTokenProvider.createTokenFromMember(member);
                    String refreshToken = jwtTokenProvider.createRefreshTokenFromMember(member);

                    TokenDto tokenDto = TokenDto.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .email(member.getEmail())
                            .build();

                    return ResponseEntity.ok(tokenDto);
                })
                .orElseThrow(() -> new BadCredentialsException("잘못된 이메일 또는 비밀번호입니다.")).getBody();
    }


    /**
     * 이메일 중복 확인
     * 특정 이메일의 중복 여부 확인
     *
     * @param email 중복 여부를 확인할 이메일
     * @return 이미 등록된 이메일이 있으면 해당 유저를 포함한 Optional, 없으면 비어있는 Optional 반환
     */

    public Optional<Member> checkEmail(String email) {
        // 이메일로 가입된 유저가 있는지 조회한 결과 반환
        return memberRepository.findByEmail(email);
    }

    /**
     * 회원가입
     * 새로운 유저를 생성하고 저장
     *
     * @param request 회원가입에 필요한 정보를 담은 DTO
     */

    public String signUp(MemberSignUpDto request) {
        // 이미 가입된 이메일인 경우 예외 처리
        if (checkEmail(request.getEmail()).isPresent()) {
            throw new EmailExistException("이미 가입된 이메일입니다.");
        }

        // 주소를 통해 x, y 값 얻어오기
        Map<String, Float> coordinates = getCoordinates(request.getAddress());


        // 유저 생성
        Member member = Member.builder()
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .address(request.getAddress())
                .latitude(coordinates.get("latitude")) // 위도
                .longitude(coordinates.get("longitude")) // 경도
                .role(MemberRole.REGULAR) // 유저 역할 -> REGULAR
                .build();

        // 유저 저장
        memberRepository.save(member);

        return "성공적으로 회원가입이 완료되었습니다.";
    }

    /**
     * 비밀번호 검색
     * @param email 비밀번호를 찾을 유저의 이메일
     * @return 이메일을 통해 찾은 유저의 비밀번호 반환
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public String findPassword(String email) {
        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 찾은 유저의 비밀번호 반환
        return member.getPassword();
    }

    public void findMemberByEmail(String email) {
        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
    }

    /**
     * 임시 비밀번호 전송
     * @param email 임시 비밀번호를 보낼 유저의 이메일
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public String sendPasswordByEmail(String email) {
        log.info("email = {}", email);
        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 0~ 9 사이의 8자리 랜덤 비밀번호 생성
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            tempPassword.append(random.nextInt(10));
        }

        // 유저의 비밀번호 변경
        member.updatePassword(passwordEncoder.encode(tempPassword));

        // 메일의 제목, 내용 설정
        String subject = "GreenHeaven 계정 비밀번호 안내";
        String content = "<p>안녕하세요, GreenHeaven입니다.</p>" +
                "<p>요청하신 계정의 임시 비밀번호는 아래와 같습니다:</p>" +
                "<p><strong>임시 비밀번호: " + tempPassword + "</strong></p>" +
                "<p>보안을 위해 바로 비밀번호를 변경하는 것을 추천드립니다.</p>";

        try {
            // 이메일 메시지 객체 MimeMessage 생성 후,
            MimeMessage message = mailSender.createMimeMessage();

            // 이메일을 쉽게 설정하기 위한 헬퍼 객체 MimeMessageHelper를 사용해 이메일 설정
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 이메일 수신자 설정
            helper.setTo(email); // 보낼 이메일

            // 이메일 제목 설정
            helper.setSubject(subject); // 제목

            // 이메일 내용 설정, HTML 형식으로 전송
            helper.setText(content, true); // 내용

            // 이메일 전송
            mailSender.send(message);
            return "임시 비밀번호가 이메일로 전송되었습니다.";
        } catch (MessagingException e) {
            // 이메일 전송 실패 시 예외 처리
            throw new RuntimeException("비밀번호 재설정 이메일 전송이 실패했습니다.", e);
        }
    }

    /**
     * 프로필 조회
     * @return 이메일을 통해 찾은 유저의 프로필 정보를 DTO에 담아 반환
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public MemberProfileResponseDto getProfile() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = getAuthenticatedMemberEmail();

        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // DTO 반환
        return MemberProfileResponseDto.builder()
                .name(member.getName())
                .oldPassword(member.getPassword())
                .address(member.getAddress())
                .build();
    }

    /**
     * 프로필 수정
     */
    public void updateProfile(MemberProfileRequestDto request) throws OldPasswordMismatchException {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = getAuthenticatedMemberEmail();

        // 이메일로 유저의 비밀번호 찾기
        String storedPassword = findPassword(email);

        // 비밀번호와 일치하지 않을 시 예외 처리
        if(!passwordEncoder.matches(request.getOldPassword(), storedPassword)) {
            throw new OldPasswordMismatchException("기본 비밀번호가 일치하지 않습니다.");
        }

        // 이메일로 유저 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 기존 이름과 같지 않으면 이름 수정
        if (!request.getName().equals(member.getName())) {
            member.updateName(request.getName());
        }

        // 기존 주소와 같지 않으면 새로운 주소를 통해 x, y 값 얻은 뒤, 주소 수정
        if (!request.getAddress().equals(member.getAddress())) {
            Map<String, Float> coordinates = getCoordinates(request.getAddress());

            member.updateAddress(request.getAddress(), coordinates);
        }

        // 새로운 비밀번호 값이 비지 않았다면 비밀번호 수정
        if (!request.getNewPassword().isBlank()) {
            member.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        }
    }

    /**
     * 주소 -> x, y 좌표 변환
     * 제공된 주소를 이용해 x, y 좌표를 반환하는 Kakao API 호출 메서드
     *
     * @param address 변환할 주소
     * @return 주소에 해당하는 x(경도), y(위도) 좌표를 포함한 Map
     */
    public Map<String, Float> getCoordinates(String address) {
        Map<String, Float> coordinates = new HashMap<>();
        try {
            // 주소를 UTF-8로 인코딩하여 카카오 API에 적합한 형식으로 변경
            address = URLEncoder.encode(address, StandardCharsets.UTF_8);

            // 카카오 주소 검색 API의 엔드포인트 생성
            String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json?page=1&size=10&query=" + address;
            URL url = new URL(apiUrl);

            // HTTP 연결 초기화 및 설정
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // HTTP GET 요청 설정
            conn.setRequestProperty("Authorization", "KakaoAK " + kakaoKey); // 인증 헤더 설정
            conn.setConnectTimeout(5000); // 연결 타임아웃 설정 (5초)
            conn.setReadTimeout(5000); // 읽기 타임아웃 설정 (5초)

            // 응답 코드 확인 및 처리
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 응답 코드 200 (성공)
                // API 응답 데이터를 읽어들이기 위한 BufferedReader 초기화
                BufferedReader jsonReader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
                );

                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = jsonReader.readLine()) != null) {
                    jsonString.append(line);
                }
                jsonReader.close();

                // JSON 문자열을 파싱하여 위도 y, 경도 x 좌표 추출
                JSONParser parser = new JSONParser();
                JSONObject document = (JSONObject) parser.parse(jsonString.toString());
                JSONArray documents = (JSONArray) document.get("documents");

                if (documents != null && !documents.isEmpty()) {
                    JSONObject position = (JSONObject) documents.get(0);
                    float lat = Float.parseFloat((String) position.get("y")); // 위도 y값 추출
                    float lon = Float.parseFloat((String) position.get("x")); // 경도 x값 추출

                    // 좌표를 HashMap에 저장
                    coordinates.put("latitude", lat);
                    coordinates.put("longitude", lon);
                }
            } else {
                System.err.println("API 응답 실패. 응답 코드: " + conn.getResponseCode());
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("주소 인코딩 실패: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.err.println("잘못된 URL 형식: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("API 호출 오류: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
        }

        return coordinates;
    }

    /**
     * 회원 탈퇴
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void quit() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = getAuthenticatedMemberEmail();

        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 찾은 유저 삭제
        memberRepository.delete(member);
    }

    /**
     * 인증된 유저의 주소 조회
     * @return 유저의 주소
     */
    public String getAuthenticatedMemberAddress() {
        // 현재 인증된 사용자의 이메일 가져오기
        String email = getAuthenticatedMemberEmail();

        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
        return member.getAddress();
    }

    /**
     * 인증된 유저의 이메일 조회
     * @return 유저의 이메일
     */
    public static String getAuthenticatedMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
