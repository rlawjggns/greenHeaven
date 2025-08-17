// src/pages/Weather.jsx
import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

// 아래는 예시 데이터이며 실제로는 props나 fetch로 받아와야 함
const exampleThreeDaysWeather = [
    {
        date: "2024-08-16", // ISO 형식 문자열
        representative: {
            temperature: 18,
            sky: { displayName: "맑음" } // .displayName
        },
        minTemp: 12,
        maxTemp: 22,
        measurements: [
            {
                time: "09:00", // "HH:mm" 문자열
                temperature: 16,
                probability: 20,
                precipitation: { displayName: "없음" },
                humidity: 58,
                winding: 3.7
            },
            {
                time: "12:00",
                temperature: 18,
                probability: 10,
                precipitation: { displayName: "없음" },
                humidity: 55,
                winding: 2.9
            },
            {
                time: "15:00",
                temperature: 22,
                probability: 40,
                precipitation: { displayName: "소나기" },
                humidity: 41,
                winding: 3.5
            }
        ]
    },
    {
        date: "2024-08-17", // ISO 형식 문자열
        representative: {
            temperature: 18,
            sky: { displayName: "맑음" } // .displayName
        },
        minTemp: 12,
        maxTemp: 22,
        measurements: [
            {
                time: "09:00", // "HH:mm" 문자열
                temperature: 16,
                probability: 20,
                precipitation: { displayName: "없음" },
                humidity: 58,
                winding: 3.7
            },
            {
                time: "12:00",
                temperature: 18,
                probability: 10,
                precipitation: { displayName: "없음" },
                humidity: 55,
                winding: 2.9
            },
            {
                time: "15:00",
                temperature: 22,
                probability: 40,
                precipitation: { displayName: "소나기" },
                humidity: 41,
                winding: 3.5
            }
        ]
    },
    {
        date: "2024-08-18", // ISO 형식 문자열
        representative: {
            temperature: 18,
            sky: { displayName: "맑음" } // .displayName
        },
        minTemp: 12,
        maxTemp: 22,
        measurements: [
            {
                time: "09:00", // "HH:mm" 문자열
                temperature: 16,
                probability: 20,
                precipitation: { displayName: "없음" },
                humidity: 58,
                winding: 3.7
            },
            {
                time: "12:00",
                temperature: 18,
                probability: 10,
                precipitation: { displayName: "없음" },
                humidity: 55,
                winding: 2.9
            },
            {
                time: "15:00",
                temperature: 22,
                probability: 40,
                precipitation: { displayName: "소나기" },
                humidity: 41,
                winding: 3.5
            }
        ]
    },
    // 2, 3일차도 같은 구조로 예시 넣기 (여기선 생략)
];

export default function Weather({
                                    address = "서울특별시 강남구",
                                    threeDaysWeather = exampleThreeDaysWeather
                                }) {
    // 날짜 포맷 yyyy-mm-dd → "월, MM월 dd일"
    function formatDate(dateStr) {
        // dateStr은 "yyyy-mm-dd" 형식
        const date = new Date(dateStr);
        // 한글 요일, 월일 반환 (예: "금, 08월 16일")
        return (
            date.toLocaleDateString("ko-KR", {
                weekday: "short",
                month: "2-digit",
                day: "2-digit"
            }).replace(/\./g, "").replace(" ", ", ")
        );
    }

    return (
        <div className="text-gray-900 min-h-screen">
            <div className="container mx-auto px-4">
                <Header />
                <main className="relative flex flex-col items-center justify-center min-h-screen mt-40">
                    {/* 주소 영역 */}
                    <h2 className="text-5xl font-extrabold text-white px-10 py-6 rounded-2xl shadow-lg mb-20 mt-40 text-center bg-gradient-to-r from-lime-600 to-green-700">
                        {address}
                    </h2>

                    {/* 3일치 요약 날씨 카드 */}
                    <div className="relative z-10 w-full max-w-6xl grid grid-cols-1 md:grid-cols-3 gap-10 p-8">
                        {threeDaysWeather.map((daily, i) => (
                            <div
                                key={i}
                                className="bg-gray-50 p-6 rounded-2xl ring-1 ring-gray-200 shadow-md hover:shadow-lg transition-transform hover:scale-[1.01] duration-200"
                            >
                                {/* 날짜 */}
                                <h2 className="text-2xl font-bold mb-4 text-center text-gray-700">
                                    {formatDate(daily.date)}
                                </h2>
                                {/* 대표 기온과 하늘 상태 */}
                                <div className="flex flex-col items-center">
                                    <div className="text-6xl font-extrabold text-lime-700">
                                        {daily.representative.temperature}&deg;C
                                    </div>
                                    <div className="text-xl text-gray-700 mt-2">
                                        {daily.representative.sky.displayName}
                                    </div>
                                </div>
                                {/* 최저/최고 기온 */}
                                <div className="flex justify-center mt-4 space-x-4">
                  <span className="text-sm text-gray-600">
                    최저 {daily.minTemp}&deg;C
                  </span>
                                    <span className="text-sm text-gray-600">
                    최고 {daily.maxTemp}&deg;C
                  </span>
                                </div>
                                {/* 시간별 기상 정보 */}
                                <div className="mt-6">
                                    <h3 className="text-lg font-semibold text-lime-700 mb-3">
                                        시간별 기상 정보
                                    </h3>
                                    <div className="grid grid-cols-2 gap-4">
                                        {daily.measurements.map((m, idx) => (
                                            <div
                                                key={idx}
                                                className="bg-gray-100 ring-1 ring-gray-200 p-4 rounded-lg shadow-sm flex flex-col items-center"
                                            >
                        <span className="text-sm font-medium text-lime-900">
                          {m.time}
                        </span>
                                                <span className="text-lg font-bold text-lime-700">
                          {m.temperature}&deg;C
                        </span>
                                                <span className="text-xs text-gray-700">
                          강수확률: {m.probability}%
                        </span>
                                                <span className="text-xs text-gray-700">
                          강수: {m.precipitation.displayName}
                        </span>
                                                <span className="text-xs text-gray-700">
                          습도: {m.humidity}%
                        </span>
                                                <span className="text-xs text-gray-700">
                          풍속: {m.winding} m/s
                        </span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </main>
                <div className="py-32"></div>
                <Footer />
            </div>
        </div>
    );
}