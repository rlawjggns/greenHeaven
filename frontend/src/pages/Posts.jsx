// src/pages/Posts.jsx
import React, { useState, useEffect } from "react";
import serverApi from "../utils/serverApi.js";

export default function Posts() {
    // 검색 입력 상태
    const [search, setSearch] = useState("");
    // [추가] 디바운싱된 검색어 상태
    const [debouncedSearch, setDebouncedSearch] = useState(search);

    const [posts, setPosts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPosts, setTotalPosts] = useState(0);
    const postsPerPage = 2; // 백엔드와 페이지 크기를 맞춰주세요.

    // [추가] 디바운싱을 위한 useEffect
    useEffect(() => {
        // 사용자가 입력을 멈추면 500ms 후에 debouncedSearch 상태를 업데이트
        const timer = setTimeout(() => {
            setDebouncedSearch(search);
        }, 500); // 0.5초 딜레이

        // 이전 타이머를 클리어하여 마지막 타이머만 실행되도록 함
        return () => {
            clearTimeout(timer);
        };
    }, [search]); // 'search' 상태가 변경될 때마다 이 effect 실행

    // API 호출
    const fetchPosts = async () => {
        try {
            // [수정] 디바운싱된 검색어로 API 요청
            const res = await serverApi.get("/posts", {
                params: { search: debouncedSearch, page: currentPage }
            });
            setPosts(res.data.content || []);
            setTotalPosts(res.data.totalElements || 0);
        } catch (err) {
            console.error(err);
        }
    };

    // [수정] 페이지나 '디바운싱된 검색어'가 변경될 때 API 호출
    useEffect(() => {
        // 페이지가 변경되거나, 디바운싱된 검색어가 변경되었을 때만 API 호출
        fetchPosts();
    }, [currentPage, debouncedSearch]);

    // 검색 이벤트 핸들러
    const handleSearch = e => {
        e.preventDefault();
        // 검색 버튼 클릭 시에는 페이지를 1로 초기화하고,
        // debouncedSearch 상태가 변경되면서 useEffect가 API를 호출하도록 유도
        setCurrentPage(1);
        setDebouncedSearch(search);
    };

    // 페이지 번호 계산 (이전과 동일)
    const totalPages = Math.max(1, Math.ceil(totalPosts / postsPerPage));
    let startPage = Math.max(1, Math.min(currentPage - 2, totalPages - 4));
    let endPage = Math.min(totalPages, startPage + 4);
    if (endPage - startPage < 4) {
        startPage = Math.max(1, endPage - 4);
    }
    const pageNumbers = [];
    for (let i = startPage; i <= endPage; i++) pageNumbers.push(i);

    const handleCreate = () => {
        window.location.href = "/posts/create";
    };

    const goPage = p => setCurrentPage(p);

    return (
        <div className="text-white min-h-screen">
            <div className="container mx-auto px-4">
                <div className="mb-16"></div>
                <h2 className="text-2xl font-semibold text-white mb-6 mt-40">
                    소통마당
                </h2>

                <div className="bg-white shadow-lg rounded-lg p-6">
                    {/* ... (UI 부분은 변경 없음) ... */}
                    <div className="flex justify-between items-center mb-6 gap-4 flex-wrap">
                        <form onSubmit={handleSearch} className="flex items-center gap-2 flex-1 min-w-0">
                            <input
                                type="text"
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
                                    <a href={`/posts/${post.id}`} className="hover:underline">
                                        {post.title}
                                    </a>
                                </td>
                                <td className="py-3 px-6">{post.views}</td>
                                <td className="py-3 px-6">{new Date(post.createDate).toLocaleDateString()}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

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
            </div>
        </div>
    );
}