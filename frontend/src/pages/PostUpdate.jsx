// src/pages/PostEdit.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";

// 더미 데이터 (실제 환경에선 props/라우터/API 등으로 전달)
const dummyPost = {
    id: 1,
    title: "첫 수확 후기",
    content: "오늘은 정말 수확이 잘 되었어요!<br/>노하우도 공유할게요.<br/>화이팅~"
};

export default function PostEdit({ post = dummyPost }) {
    const [title, setTitle] = useState(post.title);
    const [content, setContent] = useState(post.content.replace(/<br\s*\/?>/gi, "\n"));
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        // 실제 수정 API 호출 필요
        alert("수정이 완료되었습니다!");
        navigate(`/posts/${post.id}`);
    };

    return (
        <div className="bg-gray-900 text-white min-h-screen">
            <div className="container mx-auto px-4">
                <Header />

                <div className="bg-white shadow-lg rounded-lg p-6 mt-40">
                    {/* 목록으로 버튼 */}
                    <div className="mb-4">
                        <a
                            href={`/posts/${post.id}`}
                            className="text-sm text-lime-700 hover:underline font-medium"
                        >
                            ← 목록으로
                        </a>
                    </div>

                    <h2 className="text-2xl font-semibold text-gray-800 mb-6">게시글 수정</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label className="block text-gray-700 font-medium mb-2">제목</label>
                            <input
                                type="text"
                                name="title"
                                className="p-3 w-full bg-gray-100 text-gray-700 rounded-lg"
                                value={title}
                                onChange={e => setTitle(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 font-medium mb-2">내용</label>
                            <textarea
                                name="content"
                                className="p-3 w-full bg-gray-100 text-gray-700 rounded-lg h-40"
                                value={content}
                                onChange={e => setContent(e.target.value)}
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            className="bg-lime-600 text-white px-6 py-3 rounded-lg hover:bg-lime-700 focus:outline-none focus:ring-2 focus:ring-lime-500"
                        >
                            수정 완료
                        </button>
                    </form>
                </div>
                <div className="py-32"></div>
                <Footer />
            </div>
        </div>
    );
}