import React, { useRef, useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import QuitModal from "../components/modal/QuitModal.jsx";

export default function Profile() {
    // 폼 상태 예시 (실제 API 연결 필요)
    const [form, setForm] = useState({
        oldPassword: "",
        newPassword: "",
        confirmNewPassword: "",
        name: "",
        address: ""
    });
    const [errors, setErrors] = useState({});
    const [quitOpen, setQuitOpen] = useState(false);

    // 다음 주소 API 연동
    const addressRef = useRef(null);
    const handleFindAddress = () => {
        if (!window.daum?.Postcode) {
            const script = document.createElement("script");
            script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
            script.onload = handleFindAddress;
            document.body.appendChild(script);
            return;
        }
        new window.daum.Postcode({
            oncomplete: data => {
                setForm(f => ({
                    ...f,
                    address: data.roadAddress
                }));
            }
        }).open();
    };

    // 폼 핸들러
    const handleChange = e => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = e => {
        e.preventDefault();
        // 실제 정보 수정 처리 필요!
        alert("정보가 수정되었습니다.");
    };

    // 탈퇴하기 실제 처리
    const handleQuit = () => {
        // 실제 탈퇴 처리(API) 필요
        alert("회원탈퇴가 처리되었습니다.");
        setQuitOpen(false);
        // 이후 홈페이지 이동, 세션삭제 등 처리
    };

    return (
        <div className="min-h-screen text-gray-900">
            <QuitModal open={quitOpen} onClose={() => setQuitOpen(false)} onQuit={handleQuit} />
            <div className="container mx-auto px-4">
                <Header />
                <main className="flex items-center justify-center min-h-screen my-60">
                    <section className="bg-white p-8 rounded shadow-lg max-w-md w-full">
                        <form onSubmit={handleSubmit}>
                            <h2 className="flex justify-center text-2xl font-bold mb-6 text-gray-900">정보 수정</h2>

                            {/* 기존 비밀번호 입력 */}
                            <div className="mb-4">
                                <label htmlFor="old_password" className="block text-gray-700 text-sm font-bold mb-2">
                                    기존 비밀번호
                                    {errors.oldPassword && <span className="text-red-500 ml-4 text-sm">{errors.oldPassword}</span>}
                                </label>
                                <input
                                    id="old_password"
                                    type="password"
                                    name="oldPassword"
                                    value={form.oldPassword}
                                    onChange={handleChange}
                                    placeholder="기존 비밀번호"
                                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                />
                            </div>

                            {/* 새로운 비밀번호 입력 */}
                            <div className="mb-4">
                                <label htmlFor="new_password" className="block text-gray-700 text-sm font-bold mb-2">
                                    새 비밀번호
                                    {errors.newPassword && <span className="text-red-500 ml-4 text-sm">{errors.newPassword}</span>}
                                </label>
                                <input
                                    id="new_password"
                                    type="password"
                                    name="newPassword"
                                    value={form.newPassword}
                                    onChange={handleChange}
                                    placeholder="새 비밀번호"
                                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                />
                            </div>

                            {/* 새로운 비밀번호 확인 */}
                            <div className="mb-4">
                                <label htmlFor="confirm_new_password" className="block text-gray-700 text-sm font-bold mb-2">
                                    비밀번호 확인
                                    {errors.confirmNewPassword && <span className="text-red-500 ml-4 text-sm">{errors.confirmNewPassword}</span>}
                                </label>
                                <input
                                    id="confirm_new_password"
                                    type="password"
                                    name="confirmNewPassword"
                                    value={form.confirmNewPassword}
                                    onChange={handleChange}
                                    placeholder="비밀번호 확인"
                                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                />
                            </div>

                            {/* 이름 입력 */}
                            <div className="mb-4">
                                <label htmlFor="name" className="block text-gray-700 text-sm font-bold mb-2">
                                    이름
                                    {errors.name && <span className="text-red-500 ml-4 text-sm">{errors.name}</span>}
                                </label>
                                <input
                                    id="name"
                                    type="text"
                                    name="name"
                                    value={form.name}
                                    onChange={handleChange}
                                    placeholder="이름"
                                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                />
                            </div>

                            {/* 주소 입력 */}
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="sample4_roadAddress">
                                    주소
                                </label>
                                <div style={{ display: "flex", gap: 8 }}>
                                    <input
                                        ref={addressRef}
                                        id="sample4_roadAddress"
                                        type="text"
                                        name="address"
                                        value={form.address}
                                        onChange={handleChange}
                                        placeholder="도로명주소"
                                        className="shadow appearance-none border rounded flex-grow py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                    />
                                    <button
                                        type="button"
                                        onClick={handleFindAddress}
                                        className="bg-lime-600 hover:bg-lime-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline cursor-pointer"
                                    >
                                        주소 찾기
                                    </button>
                                </div>
                            </div>

                            {/* 확인 버튼 */}
                            <div className="flex justify-center">
                                <button
                                    type="submit"
                                    className="bg-lime-600 hover:bg-lime-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                                >
                                    확인
                                </button>
                            </div>
                            {/* 회원 탈퇴 링크 */}
                            <div className="flex justify-end">
                                <button
                                    type="button"
                                    className="text-red-500 font-bold my-4 hover:text-red-600"
                                    onClick={() => setQuitOpen(true)}
                                >
                                    회원탈퇴
                                </button>
                            </div>
                        </form>
                    </section>
                </main>
                <Footer />
            </div>
        </div>
    );
}