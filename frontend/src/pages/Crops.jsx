// src/pages/CropStatus.jsx
import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

// 예시 데이터 (실제로는 API 또는 props로 받아서 setState 활용)
const exampleCrops = [
    {
        id: "8fb98a44-b11f-11ee-a625-325096b39f47",
        name: "토마토",
        typeName: "과채류",
        plantDate: "2024-04-25",
        harvestDate: "2024-08-25",
        remainDays: 10,
        quantity: 21.2
    },
    {
        id: "a7d78811-9b36-4e5e-bbca-990481045a36",
        name: "양상추",
        typeName: "엽채류",
        plantDate: "2024-06-01",
        harvestDate: "2024-08-30",
        remainDays: 15,
        quantity: 12.0
    }
];

export default function CropStatus({ crops = exampleCrops }) {
    // 수확 이벤트 핸들러
    const handleHarvest = (id) => {
        if (window.confirm("정말 수확하시겠습니까?")) {
            // fetch(`/crop/harvest/${id}`, {method: "POST"}) 등 실제 API 호출
        }
    };
    // 삭제 이벤트 핸들러
    const handleDelete = (id) => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            // fetch(`/crop/delete/${id}`, {method: "DELETE"}) 등 실제 API 호출
        }
    };

    // 뒤로가기
    const goBack = () => window.history.back();

    return (
        <div className="text-gray-900 min-h-screen">
            <div className="container mx-auto px-4 py-12">
                <Header />
                <div className="mb-12"></div>
                <div className="relative max-w-5xl mx-auto bg-white shadow-md rounded-lg overflow-hidden mt-60 mb-40 border border-gray-200">
                    {/* 뒤로가기 버튼 */}
                    <button
                        onClick={goBack}
                        className="text-sm text-gray-700 absolute top-4 left-4 bg-gray-100 py-1 px-2 rounded hover:bg-gray-200 hover:text-gray-900 transition"
                    >
                        ← 뒤로가기
                    </button>
                    <div className="p-6">
                        <h2 className="text-3xl font-bold text-gray-900 mb-6 text-center">작물 현황</h2>
                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-100">
                                <tr>
                                    <th className="px-4 py-2 text-left text-sm font-semibold uppercase tracking-wider text-gray-700">작물 이름</th>
                                    <th className="px-4 py-2 text-left text-sm font-semibold uppercase tracking-wider text-gray-700">작물 종류</th>
                                    <th className="px-4 py-2 text-left text-sm font-semibold uppercase tracking-wider text-gray-700">파종일</th>
                                    <th className="px-4 py-2 text-left text-sm font-semibold uppercase tracking-wider text-gray-700">수확일</th>
                                    <th className="px-4 py-2 text-left text-sm font-semibold uppercase tracking-wider text-gray-700">남은수확일</th>
                                    <th className="px-4 py-2 text-right text-sm font-semibold uppercase tracking-wider text-gray-700">수량 (kg)</th>
                                    <th className="px-4 py-2 text-center text-sm font-semibold uppercase tracking-wider text-gray-700">수확하기</th>
                                    <th className="px-4 py-2 text-center text-sm font-semibold uppercase tracking-wider text-gray-700">삭제하기</th>
                                </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                {crops.map(crop => (
                                    <tr key={crop.id} className="hover:bg-gray-50">
                                        <td className="px-4 py-2 whitespace-nowrap">{crop.name}</td>
                                        <td className="px-4 py-2 whitespace-nowrap">{crop.typeName}</td>
                                        <td className="px-4 py-2 whitespace-nowrap">{crop.plantDate}</td>
                                        <td className="px-4 py-2 whitespace-nowrap">{crop.harvestDate}</td>
                                        <td className="px-4 py-2 whitespace-nowrap">{crop.remainDays}</td>
                                        <td className="px-4 py-2 whitespace-nowrap text-right">{crop.quantity}</td>
                                        <td className="px-4 py-2 whitespace-nowrap text-center">
                                            <button
                                                className="inline-block bg-lime-600 text-white px-3 py-1 rounded hover:bg-lime-700 transition"
                                                onClick={() => handleHarvest(crop.id)}
                                            >
                                                수확하기
                                            </button>
                                        </td>
                                        <td className="px-4 py-2 whitespace-nowrap text-center">
                                            <button
                                                className="inline-block bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 transition"
                                                onClick={() => handleDelete(crop.id)}
                                            >
                                                삭제하기
                                            </button>
                                        </td>
                                    </tr>
                                ))}
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