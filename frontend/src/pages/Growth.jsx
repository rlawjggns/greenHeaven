// src/pages/CropGrowthManage.jsx
import React, { useState, useEffect } from "react";
import serverApi from "../utils/serverApi.js";
import { useNavigate } from "react-router-dom";

const optionsMap = {
    "감자": [
        { v: 2, t: '싹이 노랗게 변함 (광합성 저하, 수확 지연)' },
        { v: 1, t: '잎 가장자리부터 말라붙음 (물 부족 징후)' },
        { v: 7, t: '줄기·잎이 축 처짐 (과다 관수로 뿌리 부패)' },
        { v: 14, t: '흑색 반점 발견 (흑썩음병 발생)' },
        { v: 5, t: '잎에 흰 가루 (잿빛곰팡이병 징후)' },
        { v: 1, t: '뿌리가 너무 작음 (영양 부족)' },
    ],
    "상추": [
        { v: 2, t: '잎 끝이 갈변함 (수분 불균형)' },
        { v: 5, t: '잎이 뻣뻣해짐 (저온 스트레스)' },
        { v: 3, t: '잎 중앙이 물러짐 (과다 관수)' },
        { v: 1, t: '해충 먹은 자국 (해충 피해)' },
        { v: 5, t: '잎이 누렇게 변함 (질소 결핍)' }
    ],
    "무": [
        { v: -3, t: '뿌리 윗부분 갈라짐 (수분 과다/온도 변화)' },
        { v: 1, t: '싹 색 변화 (비료 부족)' },
        { v: 7, t: '뿌리 겉면 얼룩 (병원균 피해)' },
        { v: 1, t: '잎이 노랗게 시듦 (수분 부족)' }
    ],
    "고구마": [
        { v: 5, t: '껍질 회색 변색 (과도한 수분)' },
        { v: 3, t: '잎이 흐물흐물 (영양 과잉/고온)' },
        { v: 14, t: '덩이줄기 움푹 패임 (검은썩음병)' },
        { v: 2, t: '해충 알 발견 (진딧물)' }
    ]
};

export default function CropGrowthManage() {
    const [crops, setCrops] = useState([]);
    const [selectedCropId, setSelectedCropId] = useState("");
    const [selectedType, setSelectedType] = useState("");
    const [adjustDays, setAdjustDays] = useState("");
    const navigate = useNavigate();

    // 서버에서 작물 목록 가져오기
    useEffect(() => {
        const fetchCrops = async () => {
            try {
                const res = await serverApi.get("/crops");
                // name과 typeName만 사용
                const data = res.data.map(c => ({
                    id: c.id,
                    name: c.name,
                    type: c.typeName
                }));
                setCrops(data);
            } catch (error) {
                console.error(error);
                alert("작물 목록을 불러오는 데 실패했습니다.");
            }
        };
        fetchCrops();
    }, []);

    const handleCropChange = e => {
        const id = e.target.value;
        setSelectedCropId(id);
        const crop = crops.find(c => c.id === id);
        setSelectedType(crop ? crop.type : "");
        setAdjustDays(""); // 옵션 초기화
    };

    const handleSubmit = async e => {
        e.preventDefault();
        if (!selectedCropId || !adjustDays) {
            alert("작물과 변동사항을 모두 선택해주세요.");
            return;
        }

        const requestBody = {
            reason: optionsMap[selectedType].find(opt => opt.v === Number(adjustDays))?.t || "",
            adjustDays: Number(adjustDays)
        };

        try {
            await serverApi.patch(`/crops/${selectedCropId}`, requestBody, {
                headers: { "Content-Type": "application/json" }
            });
            alert("변동사항이 정상 반영되었습니다.");
            setSelectedCropId("");
            setSelectedType("");
            setAdjustDays("");
            navigate("/dashboard")
        } catch (error) {
            console.error(error);
            alert("변동사항 반영에 실패했습니다.");
        }
    };

    const handleBack = () => {
        navigate(-1); // 뒤로가기
    };

    const options = selectedType ? optionsMap[selectedType] || [] : [];

    return (
        <div className="text-gray-900 min-h-screen">
            <div className="container mx-auto px-4 py-12">
                <div className="mb-16" />
                <div className="max-w-4xl mx-auto bg-white shadow-lg rounded-lg p-8 mt-60">
                    <h2 className="text-2xl font-semibold text-lime-600 mb-6">생장상태 관리</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="mb-6">
                            <label htmlFor="cropType" className="block text-gray-700 font-medium mb-2">
                                대상 농작물
                            </label>
                            <select
                                id="cropType"
                                required
                                value={selectedCropId}
                                onChange={handleCropChange}
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500"
                            >
                                <option value="">선택하세요</option>
                                {crops.map(crop => (
                                    <option key={crop.id} value={crop.id}>
                                        {crop.name} ({crop.type})
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="mb-6">
                            <label htmlFor="reason" className="block text-gray-700 font-medium mb-2">
                                변동사항 선택
                            </label>
                            <select
                                id="reason"
                                required
                                value={adjustDays}
                                onChange={e => setAdjustDays(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500"
                            >
                                <option value="">변동사항을 선택하세요</option>
                                {options.map((opt, idx) => (
                                    <option key={idx} value={opt.v}>
                                        {opt.t}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="flex justify-between">
                            <button
                                type="button"
                                onClick={handleBack}
                                className="bg-gray-300 text-gray-700 px-6 py-3 rounded-lg shadow hover:bg-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-500"
                            >
                                뒤로가기
                            </button>
                            <button
                                type="submit"
                                className="bg-lime-600 text-white px-6 py-3 rounded-lg shadow-lg hover:bg-lime-700 focus:outline-none focus:ring-2 focus:ring-lime-500"
                            >
                                반영하기
                            </button>
                        </div>
                    </form>
                </div>
                <div className="py-32"></div>
            </div>
        </div>
    );
}
