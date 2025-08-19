// src/pages/Signup.jsx
import React, { useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import serverapi from "../utils/serverapi.js";

export default function Signup() {
    const [form, setForm] = useState({
        email: "",
        password: "",
        confirmPassword: "",
        name: "",
        address: "",
    });
    const [errors, setErrors] = useState({});
    const [isDaumLoaded, setIsDaumLoaded] = useState(false);

    // 주소 찾기 버튼 클릭 시: 다음 스크립트 동적 불러오기
    const handleFindAddress = () => {
        if (!window.daum || !window.daum.Postcode) {
            const script = document.createElement('script');
            script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
            script.onload = () => handleShowPostcode();
            document.body.appendChild(script);
            setIsDaumLoaded(true);
        } else {
            handleShowPostcode();
        }
    };

    const handleShowPostcode = () => {
        new window.daum.Postcode({
            oncomplete: function(data) {
                setForm(f => ({
                    ...f,
                    address: data.roadAddress
                }));
            }
        }).open();
    };

    const handleChange = e => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async e => {
        e.preventDefault();

        // 기존 클라이언트 유효성 검사
        const newErrors = {};
        if (!form.email) newErrors.email = "이메일을 입력해주세요.";
        if (!form.password || form.password.length < 8) newErrors.password = "비밀번호는 최소 8글자 이상이어야 합니다.";
        if (form.password !== form.confirmPassword) newErrors.confirmPassword = "비밀번호가 일치하지 않습니다.";
        if (!form.name) newErrors.name = "이름을 입력해주세요.";
        if (!form.address) newErrors.address = "주소를 입력해주세요.";
        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) return;

        // 서버 호출
        try {
            const response = await serverapi.post("/member/signup", form);

            window.location.href = "/signin?signup=true"; // 로그인 페이지로 이동
        } catch (err) {
            console.error(err);
            alert("서버 오류가 발생했습니다.");
        }
    };

    return (
        <div className="container mx-auto px-4 min-h-screen flex flex-col">
            <Header />
            <main className="flex flex-1 items-center justify-center my-32">
                <div className="bg-white p-8 rounded shadow-lg max-w-md w-full">
                    <form onSubmit={handleSubmit}>
                        <h2 className="text-2xl font-bold text-center mb-6 text-gray-900">회원가입</h2>
                        {/* 이메일 */}
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                                이메일
                                {errors.email && (
                                    <span className="text-red-500 ml-4 text-sm">{errors.email}</span>
                                )}
                            </label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                id="email"
                                type="email"
                                name="email"
                                value={form.email}
                                onChange={handleChange}
                                placeholder="이메일"
                                autoComplete="email"
                            />
                        </div>
                        {/* 비밀번호 */}
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="password">
                                비밀번호
                                {errors.password && (
                                    <span className="text-red-500 ml-4 text-sm">{errors.password}</span>
                                )}
                            </label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                id="password"
                                type="password"
                                name="password"
                                value={form.password}
                                onChange={handleChange}
                                placeholder="최소 8글자 이상을 입력해주세요"
                                autoComplete="new-password"
                            />
                        </div>
                        {/* 비밀번호 확인 */}
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="confirmPassword">
                                비밀번호 확인
                                {errors.confirmPassword && (
                                    <span className="text-red-500 ml-4 text-sm">{errors.confirmPassword}</span>
                                )}
                            </label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                id="confirmPassword"
                                type="password"
                                name="confirmPassword"
                                value={form.confirmPassword}
                                onChange={handleChange}
                                placeholder="비밀번호 확인"
                                autoComplete="new-password"
                            />
                        </div>
                        {/* 이름 */}
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="name">
                                이름
                                {errors.name && (
                                    <span className="text-red-500 ml-4 text-sm">{errors.name}</span>
                                )}
                            </label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                id="name"
                                type="text"
                                name="name"
                                value={form.name}
                                onChange={handleChange}
                                placeholder="이름"
                                autoComplete="name"
                            />
                        </div>
                        {/* 도로명주소 */}
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="address">
                                주소
                                {errors.address && (
                                    <span className="text-red-500 ml-4 text-sm">{errors.address}</span>
                                )}
                            </label>
                            <div className="flex gap-2">
                                <input
                                    className="shadow appearance-none border rounded flex-grow py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                    id="sample4_roadAddress"
                                    type="text"
                                    name="address"
                                    value={form.address}
                                    onChange={handleChange}
                                    placeholder="도로명주소"
                                    autoComplete="street-address"
                                    readOnly
                                />
                                <button
                                    className="bg-lime-600 hover:bg-lime-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline cursor-pointer"
                                    type="button"
                                    onClick={handleFindAddress}
                                >
                                    주소 찾기
                                </button>
                            </div>
                        </div>
                        <div className="flex justify-center">
                            <button
                                className="bg-lime-600 hover:bg-lime-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                                type="submit"
                            >
                                가입하기
                            </button>
                        </div>
                    </form>
                </div>
            </main>
            <Footer />
        </div>
    );
}