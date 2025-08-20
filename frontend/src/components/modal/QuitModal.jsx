import React, { useContext } from "react";
import serverApi from "../../utils/serverApi.js";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext"; // JWT Context

export default function QuitModal({ open, onClose }) {
    const navigate = useNavigate();
    const { isAuthenticated, logout } = useContext(AuthContext);

    const handleQuit = async () => {
        try {
            // 1. 서버에 회원 탈퇴 요청
            await serverApi.post("/member/quit");

            // 2. 프론트에서 JWT 제거
            localStorage.removeItem("jwtToken");

            // 3. 메인 페이지로 이동
            logout();
            navigate("/");

        } catch (error) {
            console.error("회원 탈퇴 실패:", error);
            alert("회원 탈퇴 중 오류가 발생했습니다.");
        }
    };

    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-[9999]">
            <div className="bg-white p-8 rounded shadow-lg max-w-md w-full relative">
                <button
                    onClick={onClose}
                    className="absolute top-3 right-4 text-2xl text-gray-500 hover:text-gray-900 font-bold"
                    aria-label="닫기"
                >×</button>
                <h2 className="text-2xl font-bold text-gray-900 text-center mb-6">회원탈퇴</h2>
                <div className="mb-4">
                    <p className="text-sm text-gray-700 font-bold mb-2">
                        가입된 회원정보가 모두 삭제됩니다. 탈퇴 후 같은 계정으로 재가입 시 기존에 가지고 있던 정보는 복원되지 않으며, 관련 결제 정보도 사라집니다. 회원 탈퇴를 진행하시겠습니까?
                    </p>
                </div>
                <div className="flex justify-center mt-6 gap-4">
                    <button
                        className="bg-white hover:bg-gray-100 text-gray-700 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline border border-gray-400"
                        type="button"
                        onClick={onClose}
                    >
                        취소
                    </button>
                    <button
                        className="bg-red-600 hover:bg-red-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                        type="button"
                        onClick={handleQuit}
                    >
                        탈퇴하기
                    </button>
                </div>
            </div>
        </div>
    );
}
