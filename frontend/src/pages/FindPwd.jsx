// src/pages/FindPassword.jsx
import React, { useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function FindPassword() {
    // 단계 상태: 'input' | 'confirm'
    const [step, setStep] = useState('input');
    const [email, setEmail] = useState('');
    const [emailError, setEmailError] = useState('');
    const [checkEmailError, setCheckEmailError] = useState('');
    // 실제 서버 연동 시 이 값을 받아옴
    const [memberEmail, setMemberEmail] = useState('');

    // 이메일 입력 → 서버에 확인 요청
    const handleSubmit = async (e) => {
        e.preventDefault();
        setEmailError('');
        setCheckEmailError('');
        // 일반적인 이메일 유효성 검사
        if (!email) {
            setEmailError('이메일을 입력하세요.');
            return;
        }
        if (!/\S+@\S+\.\S+/.test(email)) {
            setEmailError('올바른 이메일 형식이 아닙니다.');
            return;
        }
        // 서버에 이메일 전송 > 성공 시 memberEmail 반환 (여기선 모킹)
        // 예시: const res = await fetch("/api/member/password/check", {method: "POST", body: JSON.stringify({email})})
        // 실패 시 setCheckEmailError 메시지, 성공 시 memberEmail set 및 step 이동
        // DEMO용
        setTimeout(() => {
            if (email === "notfound@example.com") {
                setCheckEmailError('입력하신 이메일로 등록된 계정이 없습니다.');
                setMemberEmail('');
            } else {
                setMemberEmail(email);
                setStep('confirm');
            }
        }, 500);
    };

    // 임시비밀번호 전송(재설정 처리)
    const handleConfirm = async (e) => {
        e.preventDefault();
        // 실제 서버 전송: fetch("/api/member/password/reset/confirm", { method: "POST", body: JSON.stringify({memberEmail}) })
        // 성공 후 안내 메시지를 따로 띄워줄 수 있음
        alert("임시 비밀번호가 이메일로 전송되었습니다.");
        // 이후 로그인 등으로 이동 가능
    };

    return (
        <div className="min-h-screen">
            <div className="container mx-auto px-4">
                <Header />
                <div className="flex items-center justify-center min-h-screen my-40">
                    <div className="bg-white p-8 rounded shadow-lg max-w-md w-full">
                        {step === 'input' ? (
                            <form onSubmit={handleSubmit}>
                                <h2 className="text-2xl font-bold text-gray-900 text-center mb-6">비밀번호 찾기</h2>
                                <div className="mb-4">
                                    <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                                        가입한 이메일을 입력하세요
                                        {emailError && (
                                            <span className="text-red-500 ml-4 text-sm">{emailError}</span>
                                        )}
                                        {checkEmailError && (
                                            <span className="text-red-500 ml-4 text-sm">{checkEmailError}</span>
                                        )}
                                    </label>
                                    <input
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                        id="email"
                                        type="email"
                                        name="email"
                                        placeholder="가입한 이메일"
                                        value={email}
                                        onChange={e => setEmail(e.target.value)}
                                        required
                                    />
                                </div>
                                <div className="flex justify-center">
                                    <button
                                        className="bg-lime-600 hover:bg-lime-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                                        type="submit"
                                    >
                                        비밀번호 찾기
                                    </button>
                                </div>
                            </form>
                        ) : (
                            <form onSubmit={handleConfirm}>
                                <h2 className="text-2xl font-bold text-gray-900 text-center mb-6">비밀번호 재설정</h2>
                                <div className="mb-4">
                                    <p className="block text-gray-700 text-sm font-bold mb-2">
                                        입력하신 정보와 일치하는 계정을 발견했습니다.
                                    </p>
                                </div>
                                <div className="mb-4">
                                    <p className="block text-gray-400 text-sm font-bold mb-2">
                                        회원정보에 등록된 아래의 이메일 주소로 임시 비밀번호를 전송하시겠습니까?
                                    </p>
                                    <p className="block text-gray-700 text-sm font-bold mb-2">{memberEmail}</p>
                                </div>
                                {/* 실제 서버에는 hidden 처리 가능 */}
                                <input type="hidden" name="memberEmail" value={memberEmail} />
                                <div className="flex justify-center mt-6">
                                    <button
                                        className="bg-lime-600 hover:bg-lime-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                                        type="submit"
                                    >
                                        계속
                                    </button>
                                </div>
                            </form>
                        )}
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    );
}