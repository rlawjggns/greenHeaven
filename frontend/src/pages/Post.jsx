import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import serverApi from "../utils/serverApi.js"; // axios 인스턴스
import Header from "../components/Header";
import Footer from "../components/Footer";

// JWT에서 이메일 추출
const getEmailFromToken = () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) return null;
    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        return payload.sub || payload.email;
    } catch {
        return null;
    }
};

export default function Post() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [post, setPost] = useState(null);
    const [comments, setComments] = useState([]);
    const [commentInput, setCommentInput] = useState("");

    const currentEmail = getEmailFromToken();

    useEffect(() => {
        // 게시글 + 댓글 가져오기
        serverApi
            .get(`/posts/${id}`)
            .then((res) => {
                setPost(res.data);
                setComments(res.data.postComments || []);
                console.log(res.data);
            })
            .catch(() => {
                alert("게시글 로딩 실패");
                navigate("/posts");
            });
    }, [id, navigate]);

    if (!post) return <p className="text-center mt-20">게시글 로딩 중...</p>;

    // 게시글 삭제
    const handleDeletePost = () => {
        if (!window.confirm("정말 게시글을 삭제하시겠습니까?")) return;
        serverApi
            .delete(`/posts/${id}`)
            .then(() => navigate("/posts"))
            .catch(() => alert("게시글 삭제 실패"));
    };

    // 게시글 수정
    const handleUpdatePost = () => navigate(`/posts/${id}/update`);

    // 댓글 작성
    const handleCommentSubmit = (e) => {
        e.preventDefault();
        if (!commentInput.trim()) return;

        serverApi
            .post(`/posts/${id}/comments`, { content: commentInput })
            .then(() => {
                // 저장 후 전체 댓글 다시 가져오기
                return serverApi.get(`/posts/${id}`);
            })
            .then((res) => {
                setComments(res.data.postComments || []);
                setCommentInput("");
                alert("댓글이 작성되었습니다.");
            })
            .catch(() => alert("댓글 작성 실패"));
    };

    // 댓글 삭제
    const handleDeleteComment = (commentId) => {
        if (!window.confirm("정말 댓글을 삭제하시겠습니까?")) return;

        serverApi
            .delete(`/posts/${id}/comments/${commentId}`)
            .then(() => setComments((prev) => prev.filter((c) => c.id !== commentId)))
            .catch(() => alert("댓글 삭제 실패"));
    };

    return (
        <div className="mt-40 min-h-screen flex flex-col text-white">
            <Header />
            <main className="flex-grow container mx-auto px-4 mt-24">
                {/* 게시글 영역 */}
                <div className="bg-white text-gray-900 rounded-lg shadow-lg p-6">
                    <div className="flex justify-between items-center mb-4">
                        <button
                            onClick={() => navigate("/posts")}
                            className="text-lime-700 hover:underline font-medium"
                        >
                            ← 목록으로
                        </button>
                        {post.memberEmail === currentEmail && (
                            <div className="space-x-2">
                                <button
                                    onClick={handleUpdatePost}
                                    className="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded"
                                >
                                    수정
                                </button>
                                <button
                                    onClick={handleDeletePost}
                                    className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded"
                                >
                                    삭제
                                </button>
                            </div>
                        )}
                    </div>
                    <h1 className="text-3xl font-bold mb-4">{post.title}</h1>
                    <div className="flex flex-wrap gap-4 text-sm text-gray-500 mb-6">
                        <div>작성자: {post.memberName}</div>
                        <div>조회수: {post.views}</div>
                        <div>작성일: {new Date(post.createdDate).toLocaleString()}</div>
                    </div>
                    <div
                        className="prose max-w-none"
                        dangerouslySetInnerHTML={{ __html: post.content }}
                    />
                </div>

                {/* 댓글 영역 */}
                <div className="bg-gray-100 text-black rounded-lg shadow-md p-6 mt-8">
                    <h2 className="text-xl font-semibold mb-4">댓글 ({comments.length})</h2>

                    {comments.length ? (
                        <div className="space-y-4">
                            {comments.map((comment) => (
                                <div
                                    key={comment.id}
                                    className="bg-white p-4 rounded-lg shadow flex justify-between"
                                >
                                    <div>
                                        <div className="font-semibold text-gray-700">{comment.memberName}</div>
                                        <div className="mt-1 text-gray-800">{comment.content}</div>
                                        <div className="mt-1 text-xs text-gray-500">
                                            {new Date(comment.createdDate).toLocaleString()}
                                        </div>
                                    </div>
                                    {comment.memberEmail === currentEmail && (
                                        <button
                                            onClick={() => handleDeleteComment(comment.id)}
                                            className="text-red-600 hover:underline text-sm ml-4"
                                        >
                                            삭제
                                        </button>
                                    )}
                                </div>
                            ))}
                        </div>
                    ) : (
                        <p className="text-gray-500">아직 댓글이 없습니다.</p>
                    )}

                    {/* 댓글 작성 폼 */}
                    <form onSubmit={handleCommentSubmit} className="mt-6 flex flex-col gap-3">
            <textarea
                value={commentInput}
                onChange={(e) => setCommentInput(e.target.value)}
                required
                placeholder="댓글을 입력하세요..."
                className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500"
            />
                        <button
                            type="submit"
                            className="bg-lime-600 hover:bg-lime-700 text-white px-6 py-2 rounded-lg w-fit"
                        >
                            댓글 작성
                        </button>
                    </form>
                </div>
            </main>
            <div className="py-32"></div>
            <Footer />
        </div>
    );
}
