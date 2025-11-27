package com.greenheaven.backend.service;

import com.greenheaven.backend.domain.*;
import com.greenheaven.backend.repository.CropRepository;
import com.greenheaven.backend.repository.MemberRepository;
import com.greenheaven.backend.repository.NotificationRepository;
import com.greenheaven.backend.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HarvestService {

    private final MemberRepository memberRepository;
    private final WeatherRepository weatherRepository;
    private final CropRepository cropRepository;
    private final NotificationService notificationService;
    private final WeatherService weatherService;
    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시에 실행
    public void adjustHarvestDatesForAllUsers() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            adjustHarvestDateForUser(member);
        }
    }

    private void adjustHarvestDateForUser(Member member) {
        // 사용자의 위치 정보로 날씨 데이터 조회
        WeatherService.LatXLngY grid = weatherService.convertGRID_GPS(0, member.getLatitude(), member.getLongitude());
        List<Weather> weathers = weatherRepository.findByLocationXAndLocationYAndDate(
                (int) grid.getX(), (int) grid.getY(), LocalDate.now().minusDays(1));

        if (weathers.isEmpty()) {
            log.info("{} 사용자의 어제 날씨 데이터가 없어 수확일 조정을 건너뜁니다.", member.getEmail());
            return;
        }

        // 날씨 분석 및 수확일 조정 로직 (예시)
        // 하루 평균 하늘 상태(맑음, 구름 많음, 흐림)를 기준으로 조정
        double averageSky = weathers.stream().mapToInt(w -> w.getSky().ordinal()).average().orElse(0);
        int adjustmentDays = 0;
        String reason = "";

        if (averageSky < 1.5) { // 평균적으로 맑은 날씨가 많았다면
            adjustmentDays = -1; // 수확일 하루 앞당김
            reason = "맑은 날씨가 지속되어";
        } else if (averageSky > 2.5) { // 평균적으로 흐린 날씨가 많았다면
            adjustmentDays = 1; // 수확일 하루 늦춤
            reason = "흐린 날씨가 지속되어";
        }

        if (adjustmentDays != 0) {
            List<Crop> crops = cropRepository.findByMember(member);
            if (crops.isEmpty()) {
                return;
            }
            for (Crop crop : crops) {
                crop.updateHarvestDate(adjustmentDays);
            }
            
            // 알림 생성
            String content = String.format("%s 작물의 수확일이 %d일 조정되었습니다. (사유: %s)",
                    crops.get(0).getName(), adjustmentDays, reason);
            if (crops.size() > 1) {
                content = String.format("%s 등 %d개 작물의 수확일이 %d일 조정되었습니다. (사유: %s)",
                        crops.get(0).getName(), crops.size(), adjustmentDays, reason);
            }

            Notification notification = Notification.builder()
                    .content(content)
                    .type(NotificationType.SYSTEM) // HARVEST_ADJUSTMENT 대신 SYSTEM 사용
                    .receiverEmail(member.getEmail())
                    .build();
            notificationRepository.save(notification);
            notificationService.send(notification);
            log.info("사용자 {}의 작물 수확일 조정: {}일 (사유: {})", member.getEmail(), adjustmentDays, reason);
        }
    }
}
