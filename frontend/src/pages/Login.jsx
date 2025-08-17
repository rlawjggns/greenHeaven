// src/pages/Login.jsx
import React, { useState } from "react";
import { useLocation, Link } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";

function useSearchParam(key) {
    const { search } = useLocation();
    const params = new URLSearchParams(search);
    return params.get(key);
}

export default function Login() {
    const logout = useSearchParam("logout");
    const signup = useSearchParam("signup");
    const findpwd = useSearchParam("findpwd");
    const error = useSearchParam("error");
    const [form, setForm] = useState({ username: "", password: "", remember: false });

    const handleChange = e => {
        const { name, value, type, checked } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value
        }));
    };

    const handleSubmit = e => {
        e.preventDefault();
        // 로그인 API 호출
    };

    return (
        <div className="container mx-auto px-4 min-h-screen flex flex-col">
            <Header />
            <main className="flex flex-1 items-center justify-center my-56">
                <div className="bg-white p-8 rounded shadow-lg max-w-md w-full">
                    <h2 className="text-2xl font-bold mb-6 text-gray-900">로그인</h2>
                    <div className="mb-4">
                        {logout && (
                            <div className="p-3 text-green-700 font-bold bg-green-100 border-green-700 border rounded">
                                로그아웃이 완료되었습니다.
                            </div>
                        )}
                        {signup && (
                            <div className="p-3 text-green-700 font-bold bg-green-100 border-green-700 border rounded">
                                회원가입이 완료되었습니다.
                            </div>
                        )}
                        {findpwd && (
                            <div className="p-3 text-green-700 font-bold bg-green-100 border-green-700 border rounded">
                                임시 비밀번호를 이메일로 전송했습니다.
                            </div>
                        )}
                    </div>
                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">아이디</label>
                            <input
                                className="shadow appearance-none border border-gray-400 rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                id="email"
                                type="text"
                                name="username"
                                placeholder="이메일"
                                value={form.username}
                                onChange={handleChange}
                                required
                                autoComplete="username"
                            />
                        </div>
                        <div className="mb-6">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="password">비밀번호</label>
                            <input
                                className="shadow appearance-none border border-gray-400 rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
                                id="password"
                                type="password"
                                name="password"
                                placeholder="비밀번호"
                                value={form.password}
                                onChange={handleChange}
                                required
                                autoComplete="current-password"
                            />
                        </div>
                        <div className="flex items-center justify-between mb-4">
                            <label className="inline-flex items-center">
                                <input
                                    type="checkbox"
                                    className="form-checkbox text-gray-600"
                                    name="remember"
                                    checked={form.remember}
                                    onChange={handleChange}
                                />
                                <span className="ml-2 text-gray-700">로그인 상태 유지</span>
                            </label>
                        </div>
                        {error && (
                            <div className="text-red-500 font-bold my-4">
                                이메일 또는 비밀번호가 올바르지 않습니다.
                            </div>
                        )}
                        <div className="flex items-center justify-between">
                            <button
                                className="bg-lime-600 hover:bg-lime-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                                type="submit"
                            >
                                로그인
                            </button>
                        </div>
                        <div className="text-center mt-4">
                            <Link
                                className="inline-block align-baseline font-bold text-sm text-lime-600 hover:text-lime-700"
                                to="/signup"
                            >
                                회원가입
                            </Link>
                            <span className="mx-2 text-gray-600">|</span>
                            <Link
                                className="inline-block align-baseline font-bold text-sm text-lime-600 hover:text-lime-700"
                                to="/login/password/find"
                            >
                                비밀번호 찾기
                            </Link>
                        </div>
                    </form>
                </div>
            </main>
            <Footer />
        </div>
    );
}