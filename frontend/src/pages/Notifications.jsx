// src/pages/Notifications.jsx
import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

// 예시용 notifications 데이터 (실제 데이터는 props, useState 등으로 관리하도록 교체 가능)
const exampleNotifications = [
    {
        id: "e4a1d2ac-1234-5678-9abc-000000000001",
        content: "오늘 강수 예보가 있습니다.",
        type: "WEATHER",
        createdDate: "2024-08-17 07:30"
    },
    {
        id: "e4a1d2ac-1234-5678-9abc-000000000002",
        content: "토마토 수확일이 내일입니다.",
        type: "CROP",
        createdDate: "2024-08-16 14:12"
    }
];

export default function Notifications({ notifications = exampleNotifications }) {
    // 삭제 이벤트
    const handleDelete = id => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            // 실제로는 API 호출 등 삭제 로직 작성 필요
            // 예시: fetch(`/notification/delete/${id}`, { method: "DELETE" })
        }
    };

    // 뒤로가기
    const goBack = () => window.history.back();

    return (
        <div className="text-gray-900 min-h-screen">
            <div className="container mx-auto px-4 py-12">
                <Header />
                <div className="mb-12" />
                <div className="relative max-w-5xl mx-auto bg-white shadow-md rounded-lg overflow-hidden mt-60 mb-40 border border-gray-200">
                    {/* 뒤로가기 버튼 */}
                    <button
                        onClick={goBack}
                        className="text-sm text-gray-700 absolute top-4 left-4 bg-gray-100 py-1 px-2 rounded hover:bg-gray-200 hover:text-gray-900 transition"
                    >
                        ← 뒤로가기
                    </button>
                    <div className="p-6">
                        <h2 className="text-3xl font-bold text-gray-900 mb-6 text-center">최근 알림</h2>
                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-100">
                                <tr>
                                    <th className="px-4 py-2 text-left text-sm font-semibold uppercase tracking-wider text-gray-700">내용</th>
                                    <th className="px-4 py-2 text-left text-sm font-semibold uppercase tracking-wider text-gray-700">유형</th>
                                    <th className="px-4 py-2 text-left text-sm font-semibold uppercase tracking-wider text-gray-700">날짜</th>
                                    <th className="px-4 py-2 text-center text-sm font-semibold uppercase tracking-wider text-gray-700">삭제하기</th>
                                </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                {notifications.length > 0 ? (
                                    notifications.map(n => (
                                        <tr key={n.id} className="hover:bg-gray-50">
                                            <td className="px-4 py-2 whitespace-nowrap">{n.content}</td>
                                            <td className="px-4 py-2 whitespace-nowrap">{n.type}</td>
                                            <td className="px-4 py-2 whitespace-nowrap">{n.createdDate}</td>
                                            <td className="px-4 py-2 whitespace-nowrap text-center">
                                                <button
                                                    className="inline-block bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 transition"
                                                    onClick={() => handleDelete(n.id)}
                                                >
                                                    삭제하기
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={4} className="text-gray-500 text-center py-4">
                                            최근 알림이 없습니다.
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    );
}