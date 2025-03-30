package com.greenheaven.greenheaven_app.service;

import com.greenheaven.greenheaven_app.domain.dto.CropRequestDto;
import com.greenheaven.greenheaven_app.domain.entity.Crop;
import com.greenheaven.greenheaven_app.domain.entity.Member;
import com.greenheaven.greenheaven_app.repository.CropRepository;
import com.greenheaven.greenheaven_app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CropService {
    private final CropRepository cropRepository;
    private final MemberRepository memberRepository;

    /**
     * 작물 등록
     * @param request 작물 등록에 필요한 정보를 담은 객체
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void createCrop(CropRequestDto request) {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = MemberService.getAuthenticatedMemberEmail();

        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        Crop crop = Crop.builder()
                .name(request.getName())
                .type(request.getType())
                .member(member)
                .plantDate(request.getPlantDate())
                .harvestDate(request.getPlantDate().plusDays(30))
                .quantity(request.getQuantity())
                .build();
        cropRepository.save(crop);
    }

    /**
     * 등록된 작물 목록 10개 조회
     * @return 작물 객체 10개
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public List<Crop> getCropListTen() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = MemberService.getAuthenticatedMemberEmail();

        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 작물들 반환
        return cropRepository.findTop10ByMember(member);
    }
}
