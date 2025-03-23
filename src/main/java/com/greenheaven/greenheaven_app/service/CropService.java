package com.greenheaven.greenheaven_app.service;

import com.greenheaven.greenheaven_app.domain.dto.CropRequestDto;
import com.greenheaven.greenheaven_app.domain.entity.Crop;
import com.greenheaven.greenheaven_app.domain.entity.CropType;
import com.greenheaven.greenheaven_app.domain.entity.User;
import com.greenheaven.greenheaven_app.repository.CropRepository;
import com.greenheaven.greenheaven_app.repository.UserRepository;
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
    private final UserRepository userRepository;


    public void createCrop(CropRequestDto request) {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = UserService.getAuthenticatedUserEmail();

        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        Crop crop = Crop.builder()
                .name(request.getName())
                .type(request.getType())
                .user(user)
                .plantDate(request.getPlantDate())
                .harvestDate(request.getPlantDate().plusDays(30))
                .quantity(request.getQuantity())
                .build();
        cropRepository.save(crop);
    }


    public List<Crop> getCropListTen() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = UserService.getAuthenticatedUserEmail();

        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 작물들 반환
        return cropRepository.findTop10ByUser(user);
    }
}
