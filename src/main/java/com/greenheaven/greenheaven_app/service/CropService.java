package com.greenheaven.greenheaven_app.service;

import com.greenheaven.greenheaven_app.dto.CropListResponsetDto;
import com.greenheaven.greenheaven_app.dto.CropRequestDto;
import com.greenheaven.greenheaven_app.domain.Crop;
import com.greenheaven.greenheaven_app.domain.Member;
import com.greenheaven.greenheaven_app.repository.CropRepository;
import com.greenheaven.greenheaven_app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<CropListResponsetDto> getCropListTen() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = MemberService.getAuthenticatedMemberEmail();

        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 작물들 10개 조회
        List<Crop> crops = cropRepository.findTop10ByMember(member);
        
        // 작물 객체 -> response 객체로 스트림 이용해 변환후 반환
        return crops.stream()
                .map((crop) -> CropListResponsetDto.builder()
                        .id(crop.getId())
                        .name(crop.getName())
                        .typeName(crop.getType().getKorName())
                        .quantity(crop.getQuantity())
                        .plantDate(crop.getPlantDate())
                        .harvestDate(crop.getHarvestDate())
                        .remainDays(ChronoUnit.DAYS.between(LocalDate.now(), crop.getHarvestDate()))
                        .build()
                ).collect(Collectors.toList());
    }


    /**
     * 등록된 작물 수확/삭제
     */
    public void deleteCrop(String cropId) {
        cropRepository.deleteById(UUID.fromString(cropId));
    }
}
