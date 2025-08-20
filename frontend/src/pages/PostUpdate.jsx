// src/pages/PostEdit.jsx
import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import serverApi from "../utils/serverApi.js"; // axios 기반 API
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function PostUpdate() {
    const navigate = useNavigate();
    const { id } = useParams();

    const [post, setPost] = useState(null);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");

    useEffect(() => {
        // 게시글 불러오기
        serverApi.get(`/posts/${id}`)
            .then(res => {
                setPost(res.data);
                setTitle(res.data.title);
                setContent(res.data.content.replace(/<br\s*\/?>/gi, "\n")); // <br> -> \n
            })
            .catch(err => {
                alert("게시글을 불러오는 중 오류가 발생했습니다.");
                console.error(err);
                navigate("/posts");
            });
    }, [id, navigate]);

    if (!post) return <p className="text-white text-center mt-40">게시글 로딩 중...</p>;

    const handleSubmit = (e) => {
        e.preventDefault();
        // 줄바꿈을 <br>로 변환 후 API 전송
        const updatedContent = content.replace(/\n/g, "<br/>");

        serverApi.patch(`/posts/${id}`, { title, content: updatedContent })
            .then(() => {
                alert("게시글이 수정되었습니다.");
                navigate(`/posts/${id}`);
            })
            .catch(err => {
                alert("게시글 수정 중 오류가 발생했습니다.");
                console.error(err);
            });
    };

    return (
        <div className="text-white min-h-screen flex flex-col">
            <Header />

            <main className="container mx-auto px-4 flex-grow mt-40">
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <div className="mb-4">
                        <button
                            onClick={() => navigate(`/posts/${id}`)}
                            className="text-sm text-lime-700 hover:underline font-medium"
                        >
                            ← 게시글로 돌아가기
                        </button>
                    </div>

                    <h2 className="text-2xl font-semibold text-gray-800 mb-6">게시글 수정</h2>

                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label className="block text-gray-700 font-medium mb-2">제목</label>
                            <input
                                type="text"
                                className="p-3 w-full bg-gray-100 text-gray-700 rounded-lg"
                                value={title}
                                onChange={e => setTitle(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-4">
                            <label className="block text-gray-700 font-medium mb-2">내용</label>
                            <textarea
                                className="p-3 w-full bg-gray-100 text-gray-700 rounded-lg h-48"
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
            </main>
            <div className="py-32"></div>
            <Footer />
        </div>
    );
}
