// src/pages/Login.jsx
import React, { useState,useEffect, useContext  } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";
import serverApi from "../utils/serverApi";
import { AuthContext } from "../context/AuthContext";
import FindPasswordModal from "../components/modal/FindPasswordModal";

export default function SignInPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const location = useLocation();
    const [signupSuccess, setSignupSuccess] = useState(false);
    const { login } = useContext(AuthContext);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        if (params.get("signup") === "true") {
            setSignupSuccess(true);
        }
    }, [location.search]);

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const params = new URLSearchParams();
            params.append("username", email);
            params.append("password", password);

            const response = await serverApi.post(`/member/signin`, params, {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                }
            });

            const { accessToken, refreshToken, email: userEmail } = response.data;

            login(accessToken); // JWT 저장 및 상태 갱신
            navigate("/");
        } catch {
            setError("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    };

    const goToSignup = () => {
        navigate("/signup"); // 원하는 경로로 이동
    };

    return (
        <div className="container mx-auto px-4 min-h-screen flex flex-col">
            <Header />
            <main className="flex flex-1 items-center justify-center my-56">
                <div className="bg-white p-8 rounded shadow-lg max-w-md w-full">
                    <h2 className="text-2xl font-bold mb-6 text-gray-900">로그인</h2>
                    <div className="mb-4">
                        {/*{logout && (*/}
                        {/*    <div className="p-3 text-green-700 font-bold bg-green-100 border-green-700 border rounded">*/}
                        {/*        로그아웃이 완료되었습니다.*/}
                        {/*    </div>*/}
                        {/*)}*/}
                        {signupSuccess && (
                            <div className="text-green-600 font-semibold text-left mb-2">
                                회원가입이 완료되었습니다.
                            </div>
                        )}
                        {/*{findpwd && (*/}
                        {/*    <div className="p-3 text-green-700 font-bold bg-green-100 border-green-700 border rounded">*/}
                        {/*        임시 비밀번호를 이메일로 전송했습니다.*/}
                        {/*    </div>*/}
                        {/*)}*/}
                    </div>
                    <form onSubmit={handleLogin}>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">아이디</label>
                            <input
                                className="shadow appearance-none border border-gray-400 rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                id="email"
                                type="text"
                                placeholder="이메일"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
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
                                placeholder="비밀번호"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
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
                                    // checked={form.remember}
                                    // onChange={handleChange}
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
                            <button
                                type="button"
                                onClick={goToSignup}
                                className="inline-block font-bold text-sm text-lime-600 hover:text-lime-700 mx-2">
                                회원가입
                            </button>
                            <span className="mx-2 text-gray-600">|</span>
                            <button
                                type="button"  // 여기서 반드시 submit이 아니라 button
                                onClick={openModal}
                                className="inline-block font-bold text-sm text-lime-600 hover:text-lime-700 mx-2"
                            >
                                비밀번호 찾기
                            </button>
                        </div>
                    </form>
                </div>
            </main>
            <Footer />
            <FindPasswordModal isOpen={isModalOpen} onClose={closeModal} />
        </div>
    );
}