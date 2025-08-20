// src/pages/Posts.jsx
import React, { useState, useEffect } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import serverApi from "../utils/serverApi.js";

export default function Posts() {
    // 검색 및 페이징 상태
    const [search, setSearch] = useState("");
    const [posts, setPosts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPosts, setTotalPosts] = useState(0);
    const postsPerPage = 2;

    // 페이지 번호 구성 (5개씩)
    const totalPages = Math.max(1, Math.ceil(totalPosts / postsPerPage));
    let startPage = Math.max(1, Math.min(currentPage - 2, totalPages - 4));
    let endPage = Math.min(totalPages, startPage + 4);
    if (endPage - startPage < 4) {
        startPage = Math.max(1, endPage - 4);
    }
    const pageNumbers = [];
    for (let i = startPage; i <= endPage; i++) pageNumbers.push(i);

    // API 호출
    const fetchPosts = async () => {
        try {
            const res = await serverApi.get("/posts", {
                params: { search, page: currentPage }
            });
            setPosts(res.data.content || []); // Page 객체 content
            setTotalPosts(res.data.totalElements || 0);
            console.log(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    // 페이지나 검색어 변경 시 API 호출
    useEffect(() => {
        fetchPosts();
    }, [currentPage, search]);

    // 검색 이벤트 핸들러
    const handleSearch = e => {
        e.preventDefault();
        setCurrentPage(1);
        fetchPosts();
    };

    // 글쓰기 버튼
    const handleCreate = () => {
        window.location.href = "/posts/create";
    };

    // 페이지 이동
    const goPage = p => setCurrentPage(p);

    return (
        <div className="text-white min-h-screen">
            <div className="container mx-auto px-4">
                <Header />
                <div className="mb-16"></div>
                <h2 className="text-2xl font-semibold text-white mb-6 mt-40">
                    소식과 이야기
                </h2>

                <div className="bg-white shadow-lg rounded-lg p-6">
                    <div className="flex justify-between items-center mb-6 gap-4 flex-wrap">
                        {/* 검색 바 */}
                        <form onSubmit={handleSearch} className="flex items-center gap-2 flex-1 min-w-0">
                            <input
                                type="text"
                                name="search"
                                className="p-3 bg-gray-100 text-gray-700 rounded-lg flex-1 min-w-0"
                                style={{ maxWidth: 400 }}
                                placeholder="검색어를 입력하세요..."
                                value={search}
                                onChange={e => setSearch(e.target.value)}
                            />
                            <button
                                type="submit"
                                className="bg-lime-600 text-white px-6 py-3 rounded-lg hover:bg-lime-700 focus:outline-none focus:ring-2 focus:ring-lime-500 transition"
                            >
                                검색
                            </button>
                        </form>

                        {/* 새 글 작성 버튼 */}
                        <button
                            onClick={handleCreate}
                            className="bg-lime-600 text-white px-6 py-3 rounded-lg hover:bg-lime-700 focus:outline-none focus:ring-2 focus:ring-lime-500 transition whitespace-nowrap ml-2"
                        >
                            새 글 작성하기
                        </button>
                    </div>

                    <table className="min-w-full table-auto mt-2">
                        <thead>
                        <tr className="bg-lime-600 text-white">
                            <th className="py-3 px-6 text-left">작성자</th>
                            <th className="py-3 px-6 text-left">제목</th>
                            <th className="py-3 px-6 text-left">조회수</th>
                            <th className="py-3 px-6 text-left">작성일</th>
                        </tr>
                        </thead>
                        <tbody className="text-gray-700">
                        {posts.map(post => (
                            <tr key={post.id}>
                                <td className="py-3 px-6">{post.memberName}</td>
                                <td className="py-3 px-6">
                                    <a
                                        href={`/posts/${post.id}`}
                                        className="hover:underline"
                                    >
                                        {post.title}
                                    </a>
                                </td>
                                <td className="py-3 px-6">{post.views}</td>
                                <td className="py-3 px-6">{new Date(post.createDate).toLocaleDateString()}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {/* 페이징 처리 */}
                    <div className="mt-6 flex justify-center">
                        {currentPage > 1 && (
                            <button
                                className="bg-lime-600 text-white px-4 py-2 rounded-lg hover:bg-lime-700 focus:outline-none focus:ring-2 focus:ring-lime-500 mr-2"
                                onClick={() => goPage(currentPage - 1)}
                            >
                                이전
                            </button>
                        )}
                        {pageNumbers.map(i => (
                            <button
                                key={i}
                                className={`px-4 py-2 mx-1 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500 transition
                                ${i === currentPage ? "bg-lime-600 text-white" : "bg-gray-100 text-black hover:bg-lime-600 hover:text-white"}`}
                                onClick={() => goPage(i)}
                            >
                                {i}
                            </button>
                        ))}
                        {currentPage < totalPages && (
                            <button
                                className="bg-lime-600 text-white px-4 py-2 rounded-lg hover:bg-lime-700 focus:outline-none focus:ring-2 focus:ring-lime-500 ml-2"
                                onClick={() => goPage(currentPage + 1)}
                            >
                                다음
                            </button>
                        )}
                    </div>
                </div>
                <div className="py-32"></div>
                <Footer />
            </div>
        </div>
    );
}
