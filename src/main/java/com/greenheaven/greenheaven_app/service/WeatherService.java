package com.greenheaven.greenheaven_app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenheaven.greenheaven_app.domain.dto.DailyForecast;
import com.greenheaven.greenheaven_app.domain.dto.WeatherDto;
import com.greenheaven.greenheaven_app.domain.dto.WeatherResponse;
import com.greenheaven.greenheaven_app.domain.entity.Precipitation;
import com.greenheaven.greenheaven_app.domain.entity.Sky;
import com.greenheaven.greenheaven_app.domain.entity.User;
import com.greenheaven.greenheaven_app.domain.entity.Weather;
import com.greenheaven.greenheaven_app.repository.UserRepository;
import com.greenheaven.greenheaven_app.repository.WeatherRepository;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    @Value("${weather.serviceKey}")
    private String serviceKey;
    private final WeatherRepository weatherRepository;
    private final UserRepository userRepository;
    private final EntityManager em;

    public static int TO_GRID = 0;
    public static int TO_GPS = 1;


    /**
     * 3일치 기상 데이터를 조회 및 생성하는 메서드
     * 인증된 사용자의 위경도 데이터를 통해 기상 데이터를 생성하거나 기존 데이터를 활용하여 반환합니다.
     *
     * @return DailyForecast 객체 리스트 (3일치 기상 데이터)
     * @throws IOException 외부 API 호출 또는 데이터 처리 중 오류 발생 시 예외를 던집니다.
     */
    public List<DailyForecast> getThreeDaysWeather() throws IOException {
        // 인증된 사용자의 이메일 가져오기
        String email = UserService.getAuthenticatedUserEmail();

        // 이메일로 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 유저의 위경도 값을 기상청 x, y 좌표로 변환
        LatXLngY latXLngY = convertGRID_GPS(0, user.getLatitude(), user.getLongitude());

        // 기존 기상 데이터 조회 (가장 오래된 날짜 기준 정렬)
        Optional<Weather> existingWeather = weatherRepository.findFirstByLocationXAndLocationYOrderByDateAsc(
                (int) latXLngY.getX(), (int) latXLngY.getY()
        );

        // 데이터가 없거나 기존 데이터가 최신 날짜가 아닌 경우 새 기상 데이터를 생성
        if (existingWeather.isEmpty() || !LocalDate.now().isEqual(existingWeather.get().getDate())) {
            weatherRepository.deleteByLocationXAndLocationY((int) latXLngY.getX(), (int) latXLngY.getY());
            createWeather((int) latXLngY.getX(), (int) latXLngY.getY());
        }

        // 생성된 데이터를 포함하여 해당 좌표의 모든 기상 데이터 조회
        List<Weather> allWeathers = weatherRepository.findByLocationXAndLocationY((int) latXLngY.getX(), (int) latXLngY.getY());

        // 날짜별로 데이터 그룹화 (key: LocalDate, value: 해당 날짜의 Weather 리스트)
        Map<LocalDate, List<Weather>> groupedByDate = allWeathers.stream()
                .collect(Collectors.groupingBy(Weather::getDate));

        // 날짜별로 대표 측정값과 최저/최고 기온을 계산하여 DailyForecast 객체 생성
        return groupedByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Weather> measurements = entry.getValue().stream()
                            .sorted(Comparator.comparing(Weather::getTime))
                            .collect(Collectors.toList());

                    // 대표 측정값: 정오(12:00)를 우선으로 선택, 없으면 첫 번째 항목 사용
                    Weather representative = measurements.stream()
                            .filter(w -> w.getTime().equals(LocalTime.of(12, 0)))
                            .findFirst()
                            .orElse(measurements.get(0));

                    // 최저/최고 기온 계산
                    double minTemp = measurements.stream()
                            .mapToDouble(Weather::getTemperature)
                            .min()
                            .orElse(representative.getTemperature());
                    double maxTemp = measurements.stream()
                            .mapToDouble(Weather::getTemperature)
                            .max()
                            .orElse(representative.getTemperature());

                    return new DailyForecast(date, representative, minTemp, maxTemp, measurements);
                })
                .sorted(Comparator.comparing(DailyForecast::getDate))
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * 위경도를 기상청 격자 X, Y 값으로 변환하거나, 반대로 변환하는 메서드
     * Lambert Conformal Conic (LCC) 방식의 투영 변환을 사용합니다.
     *
     * @param mode 변환 모드 (0: 위경도 -> 격자 좌표, 1: 격자 좌표 -> 위경도)
     * @param latitude 변환하려는 위도 (또는 격자 좌표 X 값, mode에 따라 다름)
     * @param longitude 변환하려는 경도 (또는 격자 좌표 Y 값, mode에 따라 다름)
     * @return 변환된 좌표 또는 위경도 값을 포함한 LatXLngY 객체
     */
    private LatXLngY convertGRID_GPS(int mode, double latitude, double longitude) {
        // 지구 및 격자 기본 설정 값 정의
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표 (GRID)
        double YO = 136; // 기준점 Y좌표 (GRID)

        // 각종 수학 상수 정의
        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        // 기본 계산 값 설정
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        // Lambert Conformal Conic (LCC) 방정식 계산
        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        // 반환 객체 초기화
        LatXLngY rs = new LatXLngY();

        // 변환 모드에 따라 처리
        if (mode == TO_GRID) { // 위경도 -> 격자 좌표 변환
            rs.lat = latitude; // 입력 위도
            rs.lng = longitude; // 입력 경도
            double ra = Math.tan(Math.PI * 0.25 + (latitude) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = longitude * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        } else { // 격자 좌표 -> 위경도 변환
            rs.x = latitude; // 입력 격자 X값
            rs.y = longitude; // 입력 격자 Y값
            double xn = latitude - XO;
            double yn = ro - longitude + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;
            double theta = Math.abs(xn) <= 0.0 ? 0.0 : Math.atan2(xn, yn);
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }

        return rs; // 변환 결과 반환
    }

    /**
     * 기상 정보를 생성하는 메서드
     * 제공된 X, Y 좌표와 외부 API를 호출하여 기상 데이터를 가져옵니다.
     *
     * @param x 예보 지점의 X 좌표 값
     * @param y 예보 지점의 Y 좌표 값
     * @throws IOException 외부 API 호출 또는 응답 처리 중 오류 발생 시 예외를 던집니다.
     */
    public void createWeather(Integer x, Integer y) throws IOException {
        // 어제 날짜를 "yyyyMMdd" 형식으로 포맷
        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // URL 빌더 초기화 및 외부 API 호출 주소 생성
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst");
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8)).append("=").append(serviceKey); // 서비스 키 추가
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1", StandardCharsets.UTF_8)); // 페이지 번호
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("900", StandardCharsets.UTF_8)); // 결과 수
        urlBuilder.append("&").append(URLEncoder.encode("dataType", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("JSON", StandardCharsets.UTF_8)); // 요청 데이터 형식(JSON)
        urlBuilder.append("&").append(URLEncoder.encode("base_date", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(date, StandardCharsets.UTF_8)); // 기준 날짜
        urlBuilder.append("&").append(URLEncoder.encode("base_time", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("2300", StandardCharsets.UTF_8)); // 기준 시간 (23시)
        urlBuilder.append("&").append(URLEncoder.encode("nx", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(String.valueOf(x), StandardCharsets.UTF_8)); // X 좌표
        urlBuilder.append("&").append(URLEncoder.encode("ny", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(String.valueOf(y), StandardCharsets.UTF_8)); // Y 좌표

        // URL 객체 생성
        URL url = new URL(urlBuilder.toString());

        // HTTP 연결 초기화 및 설정
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET"); // HTTP GET 요청 설정

        // 응답 코드 확인 및 처리
        if (conn.getResponseCode() == 200) { // 성공 응답 코드(200)
            processWeatherData(conn.getInputStream()); // 기상 데이터 파싱 메서드 호출
        } else {
            // 실패할 경우 예외 던지기
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다. 응답 코드: " + conn.getResponseCode());
        }
    }

    /**
     * 기상 데이터를 파싱하고 저장하는 메서드
     * 외부 API에서 가져온 데이터를 처리하여 Weather 엔티티로 변환 후 데이터베이스에 저장합니다.
     *
     * @param body 외부 API의 InputStream 데이터
     * @throws RuntimeException JSON 파싱 또는 데이터베이스 저장 중 오류 발생 시 예외를 던집니다.
     */
    private void processWeatherData(InputStream body) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Weather.WeatherBuilder> weatherMap = new HashMap<>();

        try {
            // InputStream을 WeatherResponse 객체로 변환
            WeatherResponse response = objectMapper.readValue(body, WeatherResponse.class);

            // WeatherDto 리스트를 순회하며 Weather 엔티티 빌더 생성 및 값 설정
            for (WeatherDto item : response.getItems()) {
                LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));

                // 오늘 이전이거나 오늘+3일 이후의 데이터는 무시
                if (forecastDate.isBefore(LocalDate.now()) || forecastDate.isAfter(LocalDate.now().plusDays(2))) {
                    continue;
                }

                // 그룹핑 키: 날짜와 시간 결합 (예: "202503222300")
                String key = item.getFcstDate() + item.getFcstTime();

                // 빌더가 존재하면 가져오고, 없으면 새로 생성 (기본값 설정)
                Weather.WeatherBuilder builder = weatherMap.getOrDefault(key, Weather.builder()
                        .date(forecastDate)
                        .time(LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm")))
                        .locationX(item.getNx())
                        .locationY(item.getNy())
                        .probability(0)
                        .humidity(0)
                        .temperature(0)
                        .minTemp(0.0)
                        .maxTemp(0.0)
                        .windDirection(0)
                        .winding(0.0)
                        .sky(Sky.LUCIDITY)
                        .precipitation(Precipitation.NONE)
                );

                // 카테고리별 필드 값 설정
                switch (item.getCategory()) {
                    case "POP":
                        builder.probability(Integer.parseInt(item.getFcstValue()));
                        break;
                    case "PTY":
                        int ptyVal = Integer.parseInt(item.getFcstValue());
                        builder.precipitation(
                                switch (ptyVal) {
                                    case 1 -> Precipitation.RAIN;
                                    case 2 -> Precipitation.RAINSNOW;
                                    case 3 -> Precipitation.SNOW;
                                    case 5 -> Precipitation.RAINDROP;
                                    case 6 -> Precipitation.RAINDROPBLAST;
                                    case 7 -> Precipitation.BLAST;
                                    default -> Precipitation.NONE;
                                }
                        );
                        break;
                    case "REH":
                        builder.humidity(Integer.parseInt(item.getFcstValue()));
                        break;
                    case "SKY":
                        int skyVal = Integer.parseInt(item.getFcstValue());
                        builder.sky(skyVal == 1 ? Sky.LUCIDITY : (skyVal == 3 ? Sky.CLOUDY : Sky.BLUR));
                        break;
                    case "TMP":
                        builder.temperature(Integer.parseInt(item.getFcstValue()));
                        break;
                    case "TMN":
                        builder.minTemp(Double.parseDouble(item.getFcstValue()));
                        break;
                    case "TMX":
                        builder.maxTemp(Double.parseDouble(item.getFcstValue()));
                        break;
                    case "VEC":
                        builder.windDirection(Integer.parseInt(item.getFcstValue()));
                        break;
                    case "WSD":
                        builder.winding(Double.parseDouble(item.getFcstValue()));
                        break;
                    default:
                        break;
                }

                // 맵에 빌더 저장
                weatherMap.put(key, builder);
            }

            // Weather 엔티티 생성 및 데이터베이스 저장
            for (Weather.WeatherBuilder builder : weatherMap.values()) {
                Weather weather = builder.build();
                weatherRepository.save(weather);
                log.info("저장된 Weather 엔티티: {}", weather);
            }

            // 트랜잭션 상태를 반영하고 영속성 컨텍스트 정리
            weatherRepository.flush();
            em.clear();

        } catch (Exception e) {
            throw new RuntimeException("기상 데이터 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @Getter
    static class LatXLngY {
        public double lat;
        public double lng;

        public double x;
        public double y;
    }
}
