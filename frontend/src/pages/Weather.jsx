// src/pages/Weather.jsx
import React, { useEffect, useState } from "react";
import serverApi from "../utils/serverApi.js";

export default function Weather() {
    const [weatherResponse, setWeatherResponse] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 페이지가 로드될 때 날씨 API 호출
    useEffect(() => {
        const fetchWeather = async () => {
            try {
                const res = await serverApi.get("/weather"); // Spring 백엔드 API 경로
                setWeatherResponse(res.data);
            } catch (err) {
                console.error(err);
                setError("날씨 정보를 가져오는 중 오류가 발생했습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchWeather();
    }, []);

    // 날짜 포맷 yyyy-mm-dd → "금, 08월 16일"
    function formatDate(dateStr) {
        const date = new Date(dateStr);
        return date
            .toLocaleDateString("ko-KR", {
                weekday: "short",
                month: "2-digit",
                day: "2-digit"
            })
            .replace(/\./g, "")
            .replace(" ", ", ");
    }

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p className="text-lg font-medium text-gray-700">로딩 중...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p className="text-lg font-medium text-red-500">{error}</p>
            </div>
        );
    }

    const address = weatherResponse?.address ?? "알 수 없는 위치";
    const threeDaysWeather = weatherResponse?.weathers ?? [];

    return (
        <div className="text-gray-900 min-h-screen">
            <div className="container mx-auto px-4">
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
                                        {daily.representative?.temperature}&deg;C
                                    </div>
                                    <div className="text-xl text-gray-700 mt-2">
                                        {daily.representative?.sky?.displayName}
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
                                        {daily.measurements?.map((m, idx) => (
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
                                                    강수: {m.precipitation?.displayName}
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
            </div>
        </div>
    );
}
