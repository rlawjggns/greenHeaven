import React, { useEffect, useState } from "react";
import serverApi from "../utils/serverApi.js";

import handsImg from "../assets/images/hands.jpg";
import fieldImg from "../assets/images/field.jpg";
import farmImg from "../assets/images/farm.jpg";
import houseImg from "../assets/images/house.jpg";
import writeImg from "../assets/images/write.jpg";

export default function DashBoard() {
    const [crops, setCrops] = useState([]);
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        // 작물 목록 호출
        serverApi.get("/crops/ten")
            .then(res => setCrops(res.data))
            .catch(err => console.error("작물 목록 조회 실패", err));

        // 알림 목록 호출
        serverApi.get("/notifications/ten")
            .then(res => setNotifications(res.data))
            .catch(err => console.error("알림 목록 조회 실패", err));
    }, []);

    const formatDate = (dateStr) => {
        if (!dateStr) return "-";
        const date = new Date(dateStr);
        return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')}`;
    };

    return (
        <div className="text-white min-h-screen">
            <div className="container mx-auto px-4">
                <div className="mb-16" />
                <div className="py-8 flex min-h-screen gap-6 mt-60">
                    {/* 좌측 사이드바 */}
                    <aside className="w-1/4 flex flex-col space-y-6">
                        <a href="/dashboard/crop-regist" className="relative group p-6 rounded shadow-lg h-48 border-2 border-gray-500 overflow-hidden transform transition duration-500 ease-in-out hover:scale-105 hover:shadow-2xl">
                            <img src={handsImg} className="absolute inset-0 w-full h-full object-cover transition-transform duration-500 ease-in-out group-hover:scale-110" alt="작물 등록"/>
                            <h2 className="absolute bottom-4 right-4 z-10 font-bold text-3xl text-white">새로운 작물 등록</h2>
                        </a>
                        <a href="/dashboard/growth" className="relative group p-6 rounded shadow-lg h-48 border-2 border-gray-500 overflow-hidden transform transition duration-500 ease-in-out hover:scale-105 hover:shadow-2xl">
                            <img src={fieldImg} className="absolute inset-0 w-full h-full object-cover transition-transform duration-500 ease-in-out group-hover:scale-110" alt="생장 관리"/>
                            <h2 className="absolute bottom-4 right-4 z-10 font-bold text-3xl text-white">생장상태 관리</h2>
                        </a>
                        <a href="/weather" className="relative group p-6 rounded shadow-lg h-48 border-2 border-gray-500 overflow-hidden transform transition duration-500 ease-in-out hover:scale-105 hover:shadow-2xl">
                            <img src={farmImg} className="absolute inset-0 w-full h-full object-cover transition-transform duration-500 ease-in-out group-hover:scale-110" alt="기상 정보"/>
                            <h2 className="absolute bottom-4 right-4 z-10 font-bold text-3xl text-white">기상 정보</h2>
                        </a>
                    </aside>

                    {/* 우측 대시보드 */}
                    <main className="flex-1 grid grid-cols-1 md:grid-cols-2 gap-6">
                        {/* 작물 현황 카드 */}
                        <section className="relative bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                            <img src={houseImg} className="absolute inset-0 w-full h-full object-cover opacity-20 rounded-xl" alt="작물 현황"/>
                            <h2 className="text-xl font-bold mb-3 relative z-10 text-gray-800">
                                작물 현황
                                <a href="/dashboard/crops" className="text-sm text-lime-700 absolute top-0 right-2 py-1 px-2 hover:underline">상세 보기 →</a>
                            </h2>
                            <table className="w-full text-left border-collapse relative z-10 text-gray-800 mt-10">
                                <thead>
                                <tr>
                                    <th className="px-4 py-2 border-b border-gray-300">작물</th>
                                    <th className="px-4 py-2 border-b border-gray-300">파종일</th>
                                    <th className="px-4 py-2 border-b border-gray-300">남은수확일</th>
                                </tr>
                                </thead>
                                <tbody>
                                {crops.length > 0 ? crops.map((crop) => (
                                    <tr key={crop.id}>
                                        <td className="px-4 py-2 border-b border-gray-300">{crop.typeName}</td>
                                        <td className="px-4 py-2 border-b border-gray-300">{formatDate(crop.plantDate)}</td>
                                        <td className="px-4 py-2 border-b border-gray-300">{crop.remainDays}</td>
                                    </tr>
                                )) : (
                                    <tr>
                                        <td colSpan={3} className="text-gray-500 text-center py-4">등록된 작물이 없어요 🥔</td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </section>

                        {/* 알림 카드 */}
                        <section className="relative bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                            <img src={writeImg} className="absolute inset-0 w-full h-full object-cover opacity-20 rounded-xl" alt="알림"/>
                            <h2 className="text-xl font-bold mb-3 relative z-10 text-gray-800">
                                최근 알림
                                <a href="/dashboard/notifications" className="text-sm text-lime-700 absolute top-0 right-2 py-1 px-2 hover:underline">상세 보기 →</a>
                            </h2>
                            <table className="w-full text-left border-collapse relative z-10 text-gray-800 mt-10">
                                <thead>
                                <tr>
                                    <th className="px-4 py-2 border-b border-gray-300">내용</th>
                                    <th className="px-4 py-2 border-b border-gray-300">날짜</th>
                                </tr>
                                </thead>
                                <tbody>
                                {notifications.length > 0 ? notifications.map((n) => (
                                    <tr key={n.id}>
                                        <td className="px-4 py-2 border-b border-gray-300">{n.content}</td>
                                        <td className="px-4 py-2 border-b border-gray-300">{formatDate(n.createdDate)}</td>
                                    </tr>
                                    )) : (
                                        <tr>
                                            <td colSpan={2} className="text-gray-500 text-center py-4">최근 알림이 없어요 😳</td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </section>
                    </main>
                </div>
                <div className="py-32"></div>
            </div>
        </div>
    );
}
