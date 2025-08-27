import React, { useEffect, useState } from "react";
import serverApi from "../utils/serverApi.js";

export default function CropStatus() {
    const [crops, setCrops] = useState([]);
    const [loading, setLoading] = useState(true);

    // 데이터 가져오기
    useEffect(() => {
        serverApi.get("/crops")
            .then(res => {
                setCrops(res.data); // DTO 구조와 동일하게 응답 데이터 사용
            })
            .catch(err => {
                console.error("작물 데이터를 가져오는 중 오류 발생:", err);
            })
            .finally(() => setLoading(false));
    }, []);

    const handleHarvest = (id) => {
        if (window.confirm("정말 수확하시겠습니까?")) {
            serverApi.delete(`/crops/${id}`)
                .then(() => {
                    setCrops(prev => prev.filter(c => c.id !== id));
                    alert("작물이 성공적으로 수확되었습니다.");
                })
                .catch(err => console.error(err));
        }
    };

    const handleDelete = (id) => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            serverApi.delete(`/crops/${id}`)
                .then(() => {
                    setCrops(prev => prev.filter(c => c.id !== id));
                    alert("작물이 성공적으로 삭제되었습니다.");
                })
                .catch(err => console.error(err));
        }
    };

    const goBack = () => window.history.back();

    if (loading) return <div className="text-center py-20">로딩중...</div>;

    return (
        <div className="text-gray-900 min-h-screen">
            <div className="container mx-auto px-4 py-12">
                <div className="mb-12"></div>
                <div className="relative max-w-5xl mx-auto bg-white shadow-md rounded-lg overflow-hidden mt-60 mb-40 border border-gray-200">
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
                                                수확
                                            </button>
                                        </td>
                                        <td className="px-4 py-2 whitespace-nowrap text-center">
                                            <button
                                                className="inline-block bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 transition"
                                                onClick={() => handleDelete(crop.id)}
                                            >
                                                삭제
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
