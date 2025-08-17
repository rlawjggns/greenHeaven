// src/pages/PostDetail.jsx
import React, { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";

// 예시 더미 데이터 (API 연동 시 props로 대체)
const dummyPost = {
    id: 1,
    memberName: "홍길동",
    memberEmail: "hong@green.com",
    title: "첫 수확 후기",
    content: "오늘은 정말 수확이 잘 되었어요! <br />노하우도 공유할게요.<br/>화이팅~",
    views: 42,
    createdDate: "2023-08-01 14:23",
    postComments: [
        {
            id: 1,
            memberName: "유재석",
            memberEmail: "yjs@green.com",
            content: "덕분에 저도 잘 배워갑니다!",
            createdDate: "2023-08-01 15:10"
        },
        {
            id: 2,
            memberName: "홍길동",
            memberEmail: "hong@green.com",
            content: "회원님도 풍년과 건강 농사 기원합니다.",
            createdDate: "2023-08-01 15:11"
        }
    ]
};

// 현재 로그인 이메일 예시 (실제 로그인 정보로 대체)
const currentEmail = "hong@green.com";

export default function PostDetail({ post = dummyPost }) {
    const navigate = useNavigate();
    const { id } = useParams(); // 라우트에 따라 /posts/:id 가능
    const [comments, setComments] = useState(post.postComments || []);
    const [commentInput, setCommentInput] = useState("");

    // 게시글 삭제
    const handleDeletePost = () => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            // 실제 삭제 API 필요
            alert("삭제되었습니다.");
            navigate("/posts");
        }
    };

    // 게시글 수정 이동
    const handleUpdatePost = () => {
        navigate(`/posts/${post.id}/update`);
    };

    // 댓글 작성
    const handleCommentSubmit = (e) => {
        e.preventDefault();
        if (!commentInput.trim()) return;
        const newComment = {
            id: Date.now(),
            memberName: "현재유저", // 실제 로그인 유저명으로
            memberEmail: currentEmail,
            content: commentInput,
            createdDate: new Date().toISOString().slice(0, 16).replace("T", " ")
        };
        setComments([...comments, newComment]);
        setCommentInput("");
    };

    // 댓글 삭제
    const handleDeleteComment = (commentId) => {
        if (window.confirm("이 댓글을 삭제하시겠습니까?")) {
            setComments(comments.filter(c => c.id !== commentId));
        }
    };

    return (
        <div className="bg-gray-900 text-white min-h-screen">
            <div className="container mx-auto px-4">
                <Header />

                <div className="bg-white text-gray-800 shadow-lg rounded-lg p-6 mt-40">
                    {/* 상단 버튼 영역 */}
                    <div className="flex justify-between items-center mb-6">
                        <a href="/board" className="text-sm text-lime-700 hover:underline font-medium">
                            ← 목록으로
                        </a>
                        {post.memberEmail === currentEmail && (
                            <div className="space-x-2">
                                <button
                                    className="bg-yellow-500 text-white px-4 py-2 rounded-lg hover:bg-yellow-600"
                                    onClick={handleUpdatePost}
                                >
                                    수정
                                </button>
                                <button
                                    className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
                                    onClick={handleDeletePost}
                                >
                                    삭제
                                </button>
                            </div>
                        )}
                    </div>

                    {/* 게시글 제목 */}
                    <h1 className="text-3xl font-bold mb-4">{post.title}</h1>

                    {/* 작성 정보 */}
                    <div className="flex flex-wrap gap-x-8 text-sm text-gray-500 mb-6">
                        <div>작성자: {post.memberName}</div>
                        <div>조회수: {post.views}</div>
                        <div>작성일: {post.createdDate}</div>
                    </div>

                    {/* 게시글 내용 */}
                    <div
                        className="prose max-w-none"
                        dangerouslySetInnerHTML={{ __html: post.content }}
                    />
                </div>

                {/* 댓글 섹션 */}
                <div className="bg-gray-100 shadow-md rounded-lg p-6 mt-12 text-black">
                    <h2 className="text-xl font-semibold mb-4">댓글</h2>
                    {/* 댓글 목록 */}
                    {comments.length > 0 ? (
                        <div className="space-y-4">
                            {comments.map(comment => (
                                <div key={comment.id} className="p-4 bg-white rounded-lg shadow">
                                    <div className="flex justify-between items-start">
                                        <div>
                                            <div className="text-sm text-gray-700 font-semibold mb-1">{comment.memberName}</div>
                                            <div className="text-gray-800 mb-2">{comment.content}</div>
                                            <div className="text-xs text-gray-500">{comment.createdDate}</div>
                                        </div>
                                        {/* 댓글 삭제 버튼 (본인만) */}
                                        {comment.memberEmail === currentEmail && (
                                            <button
                                                className="text-sm text-red-600 hover:underline ml-4"
                                                onClick={() => handleDeleteComment(comment.id)}
                                            >
                                                삭제
                                            </button>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-gray-500">아직 댓글이 없습니다.</div>
                    )}
                    {/* 댓글 작성 폼 */}
                    <form onSubmit={handleCommentSubmit} className="mt-6 space-y-4">
                        <textarea
                            value={commentInput}
                            onChange={e => setCommentInput(e.target.value)}
                            required
                            className="w-full p-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-lime-500"
                            placeholder="댓글을 입력하세요..."
                        />
                        <button
                            type="submit"
                            className="bg-lime-600 text-white px-6 py-2 rounded-lg hover:bg-lime-700"
                        >
                            댓글 작성
                        </button>
                    </form>
                </div>

                <div className="py-32"></div>
                <Footer />
            </div>
        </div>
    );
}