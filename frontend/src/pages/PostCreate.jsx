// src/pages/PostCreate.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function PostCreate() {
    const [form, setForm] = useState({ title: "", content: "" });
    const navigate = useNavigate();

    const handleChange = e => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = e => {
        e.preventDefault();
        // 실제 저장 처리는 API로 대체!
        // fetch("/posts/create", { method: "POST", body: JSON.stringify(form) }) 등
        alert("글이 저장되었습니다!");
        navigate("/posts");
    };

    return (
        <div className="bg-gray-900 text-white min-h-screen">
            <div className="container mx-auto px-4">
                <Header />
                <div className="bg-white shadow-lg rounded-lg p-6 mt-40">
                    {/* ← 돌아가기 */}
                    <a
                        href="/board"
                        className="text-sm text-lime-700 hover:underline font-medium mb-4 inline-block"
                    >
                        ← 목록으로
                    </a>

                    <h2 className="text-2xl font-semibold text-gray-800 mb-6">새 글 작성</h2>

                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label className="block text-gray-700 font-medium mb-2">제목</label>
                            <input
                                type="text"
                                name="title"
                                value={form.title}
                                onChange={handleChange}
                                className="p-3 w-full bg-gray-100 text-gray-700 rounded-lg"
                                required
                            />
                        </div>

                        <div className="mb-4">
                            <label className="block text-gray-700 font-medium mb-2">내용</label>
                            <textarea
                                name="content"
                                value={form.content}
                                onChange={handleChange}
                                className="p-3 w-full bg-gray-100 text-gray-700 rounded-lg h-40"
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="bg-lime-600 text-white px-6 py-3 rounded-lg hover:bg-lime-700 focus:outline-none focus:ring-2 focus:ring-lime-500"
                        >
                            저장하기
                        </button>
                    </form>
                </div>
                <div className="py-32"></div>
                <Footer />
            </div>
        </div>
    );
}