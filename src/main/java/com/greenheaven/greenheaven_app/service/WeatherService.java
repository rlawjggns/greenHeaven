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


    public void createWeather(Integer x, Integer y) throws IOException {
        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 현재 날짜  -1 지정
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("900", "UTF-8")); /*한 페이지 당 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*발표 날짜 지정(현재 날짜)*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("2300", "UTF-8")); /*23시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(String.valueOf(x), "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(String.valueOf(y), "UTF-8")); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlBuilder.toString()); // 요청 URL 지정
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // URL 커넥션 설정

        conn.setRequestMethod("GET"); // URL 매서드 지정
        if(conn.getResponseCode() == 200) { // response가 200대, 즉 성공일때
            parseWeather(conn.getInputStream());
        } else { // 실패할 경우
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.");
        }
    }

    private void parseWeather(InputStream body) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // InputStream을 NewsResponse 객체로 변환
            WeatherResponse response = objectMapper.readValue(body, WeatherResponse.class);
            // dto 리스트로 엔티티 생성 
            createWeathers(response.getItems());
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 중 오류 발생");
        }
    }

    private void createWeathers(List<WeatherDto> items) {

        // fcstDate와 fcstTime을 결합한 키를 사용하여 Weather 빌더를 저장하는 맵
        Map<String, Weather.WeatherBuilder> weatherMap = new HashMap<>();

        for (WeatherDto item : items) {
            LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            // 오늘 이전이거나 오늘+3일 이후면 무시 (3일치 데이터만 처리)
            if (forecastDate.isBefore(LocalDate.now()) || forecastDate.isAfter(LocalDate.now().plusDays(2))) {
                continue;
            }

            // 그룹핑 키: 날짜와 시간 (예: "202503222300")
            String key = item.getFcstDate() + item.getFcstTime();

            // 해당 시간대의 빌더가 존재하면 가져오고, 없으면 새로 생성 (기본값 설정)
            Weather.WeatherBuilder builder = weatherMap.getOrDefault(key, Weather.builder()
                    .date(forecastDate)
                    .time(LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm")))
                    .locationX(item.getNx())
                    .locationY(item.getNy())
                    // 필수 필드 기본값 설정 (비즈니스 로직에 맞게 조정)
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

            // 카테고리별로 해당 필드에 값을 채워 넣음
            switch (item.getCategory()) {
                case "POP": // 강수 확률
                    builder.probability(Integer.parseInt(item.getFcstValue()));
                    break;
                case "PTY": // 강수 형태
                    int ptyVal = Integer.parseInt(item.getFcstValue());
                    switch (ptyVal) {
                        case 1: builder.precipitation(Precipitation.RAIN); break;
                        case 2: builder.precipitation(Precipitation.RAINSNOW); break;
                        case 3: builder.precipitation(Precipitation.SNOW); break;
                        case 5: builder.precipitation(Precipitation.RAINDROP); break;
                        case 6: builder.precipitation(Precipitation.RAINDROPBLAST); break;
                        case 7: builder.precipitation(Precipitation.BLAST); break;
                        default: builder.precipitation(Precipitation.NONE); break;
                    }
                    break;
                case "REH": // 습도
                    builder.humidity(Integer.parseInt(item.getFcstValue()));
                    break;
                case "SKY": // 하늘 상태
                    int skyVal = Integer.parseInt(item.getFcstValue());
                    if (skyVal == 1) {
                        builder.sky(Sky.LUCIDITY);
                    } else if (skyVal == 3) {
                        builder.sky(Sky.CLOUDY);
                    } else if (skyVal == 4) {
                        builder.sky(Sky.BLUR);
                    }
                    break;
                case "TMP": // 1시간 기온
                    builder.temperature(Integer.parseInt(item.getFcstValue()));
                    break;
                case "TMN": // 일 최저기온
                    builder.minTemp(Double.parseDouble(item.getFcstValue()));
                    break;
                case "TMX": // 일 최고기온
                    builder.maxTemp(Double.parseDouble(item.getFcstValue()));
                    break;
                case "VEC": // 풍향
                    builder.windDirection(Integer.parseInt(item.getFcstValue()));
                    break;
                case "WSD": // 풍속
                    builder.winding(Double.parseDouble(item.getFcstValue()));
                    break;
                default: // 처리하지 않는 카테고리는 무시
                    break;
            }
            // 맵에 집어넣기
            weatherMap.put(key, builder);
        }

        // 그룹핑된 각 빌더에서 Weather 엔티티 생성 후 DB에 저장
        for (Weather.WeatherBuilder builder : weatherMap.values()) {
            Weather weather = builder.build();
            weatherRepository.save(weather);
            log.info("생성된 Weather: {}", weather);
        }
        
        // getThreeDayForeCast() 메서드의 기존 트랜잭션이 전파된 것이므로, 플러시를 하지 않으면 이 메서드가 종료된 후 getThreeDayForeCast()의 메서드에서 조회해봤자 원하는 결과가 일어나지 안흠 <- 트러블 슈팅 완료
        // 알고보니 같은 트랜잭션이라 영속성 컨텍스트에 저장되기 때문에, 조회해도 이상이 필요없다...
        // 그래도 동시성 이슈 방지, 영속성 컨텍스트가 커질 경우 메모리 효율성을 위해 플러시 호출해도 됨
        weatherRepository.flush();
        // 클리어, 이후 로직은 깔끔하게 db에 완전히 반영된 후의 결과를 보고싶음
        em.clear();
    }


    public List<DailyForecast> getThreeDayForecast() throws IOException {
        String email = UserService.getAuthenticatedUserEmail();

        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 유저의 위경도값을 기상청 x, y값으로 변환
        LatXLngY latXLngY = convertGRID_GPS(0, user.getLatitude(), user.getLongitude());

        // 변환한 x 값과 y값을 통해 조회하고, 날짜가 오래된 순으로 정렬한 후 첫번째 값만 가져오기
        Optional<Weather> existingWeather = weatherRepository.findFirstByLocationXAndLocationYOrderByDateAsc(
                (int)latXLngY.getX(),(int)latXLngY.getY()
        );

        // x,y 값으로 데이터가 존재하지 않거나, 존재하는 데이터의 가장 과거 날짜와 오늘 날짜가 다르다면 기존 데이터를 날리고 최신 데이터를 생성
        if (existingWeather.isEmpty() || !LocalDate.now().isEqual(existingWeather.get().getDate())) {
            weatherRepository.deleteByLocationXAndLocationY((int)latXLngY.getX(),(int)latXLngY.getY());
            createWeather((int)latXLngY.getX(),(int)latXLngY.getY());
        }

        // 생성되거나, 생성되지 않든 이후의 로직 실행
        // DB에서 X값과 Y값으로 데이터를 조회 (예: 72개 행)
        List<Weather> allWeathers = weatherRepository.findByLocationXAndLocationY((int)latXLngY.getX(),(int)latXLngY.getY());

        // 날짜별 그룹화 (key: LocalDate, value: 해당 날짜의 Weather 리스트)
        Map<LocalDate, List<Weather>> groupedByDate = allWeathers.stream()
                .collect(Collectors.groupingBy(Weather::getDate));

        // 각 날짜 그룹에 대해 대표 측정값과 최저/최고 기온을 산출한 뒤 DailyForecast 객체로 변환
        List<DailyForecast> dailyForecasts = groupedByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    // 각 날짜의 measurements를 시간별로 정렬
                    List<Weather> measurements = entry.getValue().stream()
                            .sorted(Comparator.comparing(Weather::getTime))
                            .collect(Collectors.toList());

                    // 대표 측정값: 정오(12:00)를 우선으로, 없으면 첫 번째 항목 사용
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


        return dailyForecasts;
    }

    private LatXLngY convertGRID_GPS(int mode, double latitude, double longitude) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기준점 Y좌표(GRID)

        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, latitude:위도,  longitude:경도), "TO_GPS"(좌표->위경도,  latitude:x, longitude:y) )

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GRID) {
            rs.lat = latitude;
            rs.lng = longitude;
            double ra = Math.tan(Math.PI * 0.25 + (latitude) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = longitude * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        }

        else {
            rs.x = latitude;
            rs.y = longitude;
            double xn = latitude - XO;
            double yn = ro - longitude + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;
            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }

        return rs;
    }

    @Getter
    class LatXLngY {
        public double lat;
        public double lng;

        public double x;
        public double y;
    }
}
