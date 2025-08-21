import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import serverAPi from "../../utils/serverApi.js";

export default function CropRegisterModal({ open, onClose, defaultType }) {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        name: "",
        type: defaultType || "",
        plantDate: "",
        quantity: "",
    });

    useEffect(() => {
        setForm((prev) => ({ ...prev, type: defaultType || "" }));
    }, [defaultType]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            // Spring 백엔드 API 호출
            await serverAPi.post("/crops", {
                name: form.name,
                type: form.type,
                plantDate: form.plantDate,
                quantity: parseFloat(form.quantity),
            });

            alert("농작물이 등록되었습니다!");
            navigate("/dashboard");
            onClose();
        } catch (error) {
            console.error("작물 등록 실패:", error);
            alert("작물 등록에 실패했습니다. 다시 시도해주세요.");
        }
    };

    if (!open) return null;

    return (
        <div
            className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-[9999]"
            tabIndex={-1}
        >
            <div className="bg-white text-gray-900 shadow-lg rounded-lg w-full max-w-xl p-8 relative animate-fade-in">
                <button
                    type="button"
                    className="absolute top-4 right-6 text-2xl font-bold text-gray-500 hover:text-gray-900 z-[10000]"
                    onClick={onClose}
                    aria-label="닫기"
                    tabIndex={0}
                >
                    ×
                </button>
                <h2 className="text-2xl font-semibold text-lime-600 mb-6 text-center">
                    농작물 등록
                </h2>
                <form onSubmit={handleSubmit}>
                    <div className="mb-6">
                        <label htmlFor="crop-name" className="block text-gray-700">
                            농작물 이름
                        </label>
                        <input
                            type="text"
                            id="crop-name"
                            name="name"
                            value={form.name}
                            onChange={handleChange}
                            className="w-full p-3 mt-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500"
                            placeholder="농작물 이름을 입력하세요"
                            required
                        />
                    </div>
                    <div className="mb-6">
                        <label htmlFor="crop-type" className="block text-gray-700">
                            농작물 종류
                        </label>
                        <select
                            id="crop-type"
                            name="type"
                            value={form.type}
                            onChange={handleChange}
                            className="w-full p-3 mt-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500"
                            required
                        >
                            <option value="">--선택--</option>
                            <option value="LETTUCE">상추</option>
                            <option value="RADISH">무</option>
                            <option value="POTATO">감자</option>
                            <option value="SWEETPOTATO">고구마</option>
                        </select>
                    </div>
                    <div className="mb-6">
                        <label htmlFor="plant-date" className="block text-gray-700">
                            파종일
                        </label>
                        <input
                            type="date"
                            id="plant-date"
                            name="plantDate"
                            value={form.plantDate}
                            onChange={handleChange}
                            className="w-full p-3 mt-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500"
                            required
                        />
                    </div>
                    <div className="mb-6">
                        <label htmlFor="crop-quantity" className="block text-gray-700">
                            농작물 수량(kg)
                        </label>
                        <input
                            type="number"
                            min={0}
                            step="any"
                            id="crop-quantity"
                            name="quantity"
                            value={form.quantity}
                            onChange={handleChange}
                            className="w-full p-3 mt-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500"
                            placeholder="농작물 수량을 입력하세요"
                            required
                        />
                    </div>
                    <div className="mb-6 text-right">
                        <button
                            type="submit"
                            className="bg-lime-600 text-white px-6 py-3 rounded-lg shadow-lg hover:bg-lime-700 focus:outline-none focus:ring-2 focus:ring-lime-500"
                        >
                            등록하기
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
