// src/pages/PostCreate.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import serverApi from "../utils/serverApi.js";
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function PostCreate() {
    const [form, setForm] = useState({ title: "", content: "" });
    const navigate = useNavigate();

    const handleChange = e => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async e => {
        e.preventDefault();
        try {
            await serverApi.post("/posts", form); // 백엔드 POST 요청
            alert("글이 저장되었습니다!");
            navigate("/posts");
        } catch (error) {
            console.error(error);
            alert("글 저장 중 오류가 발생했습니다.");
        }
    };

    return (
        <div className="text-white min-h-screen">
            <div className="container mx-auto px-4">
                <Header />
                <div className="bg-white shadow-lg rounded-lg p-6 mt-40">
                    <a
                        href="/posts"
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
