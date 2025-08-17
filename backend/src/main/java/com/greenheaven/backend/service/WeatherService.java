package com.greenheaven.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenheaven.backend.dto.DailyWeather;
import com.greenheaven.backend.dto.WeatherResponse;
import com.greenheaven.backend.domain.Precipitation;
import com.greenheaven.backend.domain.Sky;
import com.greenheaven.backend.domain.Member;
import com.greenheaven.backend.domain.Weather;
import com.greenheaven.backend.repository.MemberRepository;
import com.greenheaven.backend.repository.WeatherRepository;
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
    private final MemberRepository memberRepository;
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
    public List<DailyWeather> getThreeDaysWeather() throws IOException {
        // 인증된 사용자의 이메일 가져오기
        String email = MemberService.getAuthenticatedMemberEmail();

        // 이메일로 유저 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 유저의 위도 경도 -> 기상청 x, y 좌표로 변환
        LatXLngY latXLngY = convertGRID_GPS(0, member.getLatitude(), member.getLongitude());

        // 기존 기상 데이터 조회 (가장 오래된 날짜 기준 정렬)
        Optional<Weather> existingWeather = weatherRepository.findFirstByLocationXAndLocationYOrderByDateAsc(
                (int) latXLngY.getX(), (int) latXLngY.getY()
        );

        // 정렬된 기존 데이터가 있고, 첫번째 날짜가 최신 날짜가 아닌 경우에는 기존 데이터를 삭제
        if (existingWeather.isPresent() && !LocalDate.now().isEqual(existingWeather.get().getDate())) {
            weatherRepository.deleteByLocationXAndLocationY((int) latXLngY.getX(), (int) latXLngY.getY());
        }

        // 삭제가 되어서 없거나, 원래부터 데이터가 없었다면
        // 날씨 API 호출 -> 파싱 후 DB저장을 통해 3일간의 새 기상 데이터를 생성
        if (existingWeather.isEmpty()) {
            callWeatherAPI((int) latXLngY.getX(), (int) latXLngY.getY());
        }

        // 해당 좌표의 모든 기상 데이터 조회
        List<Weather> weathers = weatherRepository.findByLocationXAndLocationY((int) latXLngY.getX(), (int) latXLngY.getY());

        // 날짜별 Map으로 데이터 그룹화 (키 : LocalDate, 값: 해당 날짜의 Weather 리스트)
        Map<LocalDate, List<Weather>> groupedByDay = weathers.stream() // weather를 스트림으로 순회하여 (키 : LocalDate, 값: 해당 날짜의 Weather 리스트)로 그룹화한다.
                .collect(Collectors.groupingBy(Weather::getDate)); // 최종 연산 -> 날짜 기준으로 그룹화 -> Map<LocalDate, List<Weather>>



        // 날짜별로 대표 측정값과 최저/최고 기온을 계산하여 DailyForecast 객체 생성
        // Map은 Iteratable을 구현하지 않아 스트림 순회 불가능 -> entrySet으로 변환 -> Map의 모든 키-값 쌍(Entry)의 집합 -> <LocalDate, List<Weather>> 총 3개의 집합
        return groupedByDay.entrySet().stream() // entrySet을 스트림으로 순회하여 DailyWeather라는 값으로 변환하고, 정렬하여 총 3개의 List<Dayweather>로 가져온다
                .map(entry -> { // 중간 연산 -> 각 entry를 DailyWeather로 변환
                    LocalDate date = entry.getKey(); // entry에서 키 꺼내기(날짜) -> date
                    List<Weather> measurements = entry.getValue().stream() // entry의 값을 스트림으로 순회하여 정렬한다. 값은 24개의 List<Weather>이므로 순회가능
                            .sorted(Comparator.comparing(Weather::getTime)) // 중간연산 -> sorted를 통해 날짜 순으로 정렬한다
                            .collect(Collectors.toList()); // 최종연산 -> 다시 리스트로 반환 -> measurements
                    
                    Weather representative = measurements.stream() // measurements를 스트림으로 순회하여 대표값을 가져온다
                            .filter(w -> w.getTime().equals(LocalTime.of(12, 0))) // 중간연산 -> filter를 통해 정오 값만 필터링
                            .findFirst() // 최종연산 -> 첫 번째 값 찾기
                            .orElse(measurements.get(0)); // 정오 값이 없으면, 가장 첫번째 값을 가져온다.

                    Integer minTemp = measurements.stream() // measurements를 스트림으로 순회하여 최소기온을 가져온다
                            .mapToInt(Weather::getTemperature) // 중간 연산 -> 온도를 실수형으로 변환
                            .min() // 최종 연산 -> 최소값
                            .orElse(representative.getTemperature()); // 값이 없으면 대표값의 온도를 계산한다.
                    Integer maxTemp = measurements.stream() // measurements를 스트림으로 순회하여 최고기온을 가져온다
                            .mapToInt(Weather::getTemperature) // 중간 연산 -> 온도를 실수형으로 변환
                            .max() // 최종 연산 -> 최댓값
                            .orElse(representative.getTemperature()); // 값이 없으면 대표값의 온도를 계산한다.

                    // 스트림을 통해 가져온 날짜, 대표값, 최소기온, 최대기온, 측정값들을 각각 담는다.
                    return new DailyWeather(date, representative, minTemp, maxTemp, measurements);
                })
                .sorted(Comparator.comparing(DailyWeather::getDate)) // 중간 연산 -> 날짜 순으로 정렬
                .limit(3) // 중간 연산 -> 3개까지
                .collect(Collectors.toList()); // 최종 연산 -> 리스트로 반환 -> List<DailyWeather>
    }

    /**
     * 위경도를 기상청 격자 X, Y 값으로 변환하거나, 반대로 변환하는 메서드
     * Lambert Conformal Conic (LCC) 방식의 투영 변환을 사용한다
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
     * 기상 정보 생성
     * 제공된 X(위도), Y(경도) 좌표로 기상처 API를 호출하여 기상 데이터 조회
     *
     * @param x 예보 지점의 X(위도) 좌표 값
     * @param y 예보 지점의 Y(경도) 좌표 값
     * @throws IOException 외부 API 호출 또는 응답 처리 중 오류 발생 시 예외를 던진다
     */
    public void callWeatherAPI(Integer x, Integer y) throws IOException {
        // 어제 날짜를 "yyyyMMdd" 형식으로 포맷
        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // URL 빌더 초기화 및 외부 API 호출 주소 생성
        String urlBuilder = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + serviceKey + // 서비스 키 추가
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) + // 페이지 번호
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("900", StandardCharsets.UTF_8) + // 결과 수
                "&" + URLEncoder.encode("dataType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("JSON", StandardCharsets.UTF_8) + // 요청 데이터 형식(JSON)
                "&" + URLEncoder.encode("base_date", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(date, StandardCharsets.UTF_8) + // 기준 날짜
                "&" + URLEncoder.encode("base_time", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("2300", StandardCharsets.UTF_8) + // 기준 시간 (23시)
                "&" + URLEncoder.encode("nx", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(x), StandardCharsets.UTF_8) + // X 좌표
                "&" + URLEncoder.encode("ny", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(y), StandardCharsets.UTF_8); // Y 좌표

        // URL 객체 생성
        URL url = new URL(urlBuilder);

        // HTTP 연결 초기화 및 설정
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET"); // HTTP GET 요청 설정

        // 응답 코드 확인 및 처리
        if (conn.getResponseCode() == 200) { // 성공 응답 코드(200)
            createWeather(conn.getInputStream()); // 기상 데이터 파싱 메서드 호출
        } else {
            // 실패할 경우 예외 던지기
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다. 응답 코드: " + conn.getResponseCode());
        }
    }

    /**
     * JSON 기상 데이터를 파싱하고 DB에 저장
     * 기상청 API에서 가져온 JSON 형식의 데이터를 파싱하여 처리한 뒤, Weather 엔티티로 변환 후 데이터베이스에 저장한다
     *
     * @param body 외부 API의 InputStream 데이터
     * @throws RuntimeException JSON 파싱 또는 데이터베이스 저장 중 오류 발생 시 예외를 던집니다.
     */
    private void createWeather(InputStream body) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Weather.WeatherBuilder> map = new HashMap<>();

        try {
            // InputStream을 WeatherResponse 객체로 변환
            WeatherResponse response = objectMapper.readValue(body, WeatherResponse.class);

            // WeatherDto 리스트를 순회하며 Weather 엔티티 빌더 생성 및 값 설정
            for (WeatherResponse.Item item : response.getItems()) {
                LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));

                // 오늘 이전이거나 오늘+3일 이후의 데이터는 무시
                if (forecastDate.isBefore(LocalDate.now()) || forecastDate.isAfter(LocalDate.now().plusDays(2))) {
                    continue;
                }

                // Map에서 사용할 키: 날짜 + 시간(20250322 + 2300 -> 202503222300)
                String key = item.getFcstDate() + item.getFcstTime();

                // 키로 빌더를 조회해 빌더가 존재하면 가져오고, 없으면 새로 생성 (기본값 설정)
                Weather.WeatherBuilder builder = map.getOrDefault(key, Weather.builder()
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
                    case "POP": // 시간 별 강수확률
                        builder.probability(Integer.parseInt(item.getFcstValue()));
                        break;
                    case "PTY": // 시간 별 강수형태
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
                    case "REH": // 시간 별 습도
                        builder.humidity(Integer.parseInt(item.getFcstValue()));
                        break;
                    case "SKY": // 시간 별 하늘상태
                        int skyVal = Integer.parseInt(item.getFcstValue());
                        builder.sky(skyVal == 1 ? Sky.LUCIDITY : (skyVal == 3 ? Sky.CLOUDY : Sky.BLUR));
                        break;
                    case "TMP": // 시간 별 기온
                        builder.temperature(Integer.parseInt(item.getFcstValue()));
                        break;
                    case "TMN": // 일 최저기온
                        builder.minTemp(Double.parseDouble(item.getFcstValue()));
                        break;
                    case "TMX": // 일 최고기온
                        builder.maxTemp(Double.parseDouble(item.getFcstValue()));
                        break;
                    case "VEC": // 시간 별 풍향
                        builder.windDirection(Integer.parseInt(item.getFcstValue()));
                        break;
                    case "WSD": // 시간 별 풍속
                        builder.winding(Double.parseDouble(item.getFcstValue()));
                        break;
                    default:
                        break;
                }

                // 맵에 빌더 저장
                map.put(key, builder);
            }

            // map의 값(Weather.Builder)들을 꺼내 Weather 엔티티 생성 및 데이터베이스 저장
            for (Weather.WeatherBuilder builder : map.values()) {
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
        public double lat; // 위도
        public double lng; // 경도

        public double x; // 위도
        public double y; // 경도
    }
}
