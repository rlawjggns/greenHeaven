import React, { useState, useEffect } from "react";
import serverApi from "../../utils/serverApi";

export default function FindPasswordModal({ isOpen, onClose }) {
    const [step, setStep] = useState('input');
    const [email, setEmail] = useState('');
    const [emailError, setEmailError] = useState('');
    const [checkEmailError, setCheckEmailError] = useState('');
    const [memberEmail, setMemberEmail] = useState('');

    // 모달이 열릴 때 상태 초기화
    useEffect(() => {
        if (isOpen) {
            setStep('input');
            setEmail('');
            setEmailError('');
            setCheckEmailError('');
            setMemberEmail('');
        }
    }, [isOpen]);

    if (!isOpen) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setEmailError('');
        setCheckEmailError('');

        try {
            // 서버에 이메일 존재 여부 확인 요청
            const response = await serverApi.get(`/member/email/${email}`);
            if (response.status === 200) {
                setMemberEmail(email);
                setStep('confirm');
            }
        } catch (error) {
            if (error.response && error.response.status === 404) {
                setCheckEmailError('입력하신 이메일로 등록된 계정이 없습니다.');
                setMemberEmail('');
            } else {
                setCheckEmailError('이메일 확인 중 오류가 발생했습니다.');
            }
        }
    };


    const handleConfirm = async (e) => {
        e.preventDefault();
        try {
            const response = await serverApi.post(`/member/password-reset/${email}`);
            alert(response.data); // 서버에서 오는 "임시 비밀번호 전송됨" 같은 메시지
            onClose(); // 모달 닫기
        } catch (error) {
            if (error.response && error.response.status === 404) {
                alert("해당 이메일을 찾을 수 없습니다.");
            } else {
                alert("비밀번호 재설정 중 오류가 발생했습니다.");
            }
        }
    };

    return (
        <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50">
            <div className="bg-white p-8 rounded shadow-lg max-w-md w-full relative">
                <button
                    onClick={onClose}
                    className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                >
                    ✕
                </button>
                {step === 'input' ? (
                    <form onSubmit={handleSubmit}>
                        <h2 className="text-2xl font-bold text-gray-900 text-center mb-6">비밀번호 찾기</h2>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                                가입한 아이디를 입력하세요
                                </label>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                                {emailError && <span className="text-red-500 mt-4 text-sm">{emailError}</span>}
                                {checkEmailError && <span className="text-red-500 mt-4 text-sm">{checkEmailError}</span>}
                            </label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                id="email"
                                type="email"
                                placeholder="이메일"
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
                        <p className="text-gray-700 text-sm mb-2">입력하신 정보와 일치하는 계정을 발견했습니다.</p>
                        <p className="text-gray-400 text-sm mb-2">현재 비밀번호를 초기화하고 아래 이메일로 임시 비밀번호를 전송하시겠습니까?</p>
                        <p className="text-gray-700 text-sm font-bold mb-4">{memberEmail}</p>
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
    );
}
