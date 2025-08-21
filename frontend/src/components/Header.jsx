// frontend/src/components/Header.jsx
import React, { useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import headerLogoImg from "../assets/images/logo.png";
import { AuthContext } from "../context/AuthContext"; // JWT Context

const Header = () => {
    const { isAuthenticated, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = (e) => {
        e.preventDefault();
        logout();       // JWT 제거 + 상태 초기화
        navigate("/");  // 홈으로 이동
    };

    // 공통 클릭 핸들러 (로그인 여부 검사)
    const handleProtectedNav = (e, path) => {
        e.preventDefault();
        if (!isAuthenticated) {
            navigate("/signin"); // 로그인 안했으면 로그인 페이지로
        } else {
            navigate(path); // 로그인 했으면 원래 경로로
        }
    };

    return (
        <header className="flex justify-between items-center pt-4 z-50 fixed top-0 left-0 w-full bg-transparent">
            {/* 로고 */}
            <div className="flex items-center ml-10">
                <Link to="/" className="block">
                    <img src={headerLogoImg} alt="Logo" className="h-32 mr-5" />
                </Link>
            </div>

            {/* 네비게이션 */}
            <nav className="flex items-center justify-between w-full mr-10">
                <ul className="flex space-x-5 ml-auto">
                    <li>
                        <a
                            href="/about"
                            onClick={(e) => handleProtectedNav(e, "/about")}
                            className="text-gray-300 hover:text-gray-400 transition duration-500"
                        >
                            기능소개
                        </a>
                    </li>
                    <li>
                        <a
                            href="/dashboard"
                            onClick={(e) => handleProtectedNav(e, "/dashboard")}
                            className="text-gray-300 hover:text-gray-400 transition duration-500"
                        >
                            작물관리
                        </a>
                    </li>
                    <li>
                        <a
                            href="/weather"
                            onClick={(e) => handleProtectedNav(e, "/weather")}
                            className="text-gray-300 hover:text-gray-400 transition duration-500"
                        >
                            기상정보
                        </a>
                    </li>
                    <li>
                        <a
                            href="/news"
                            onClick={(e) => handleProtectedNav(e, "/news")}
                            className="text-gray-300 hover:text-gray-400 transition duration-500"
                        >
                            농업소식
                        </a>
                    </li>
                    <li>
                        <a
                            href="/posts"
                            onClick={(e) => handleProtectedNav(e, "/posts")}
                            className="text-gray-300 hover:text-gray-400 transition duration-500"
                        >
                            소통마당
                        </a>
                    </li>

                    {/* 로그인 상태에 따라 표시 */}
                    {!isAuthenticated ? (
                        <li>
                            <Link
                                to="/signin"
                                className="text-gray-300 hover:text-gray-400 transition duration-500"
                            >
                                로그인
                            </Link>
                        </li>
                    ) : (
                        <>
                            <li>
                                <Link
                                    to="/profile"
                                    className="text-gray-300 hover:text-gray-400 transition duration-500"
                                >
                                    프로필
                                </Link>
                            </li>
                            <li>
                                <a
                                    href="/logout"
                                    className="text-gray-300 hover:text-gray-400 transition duration-500"
                                    onClick={handleLogout}
                                >
                                    로그아웃
                                </a>
                            </li>
                        </>
                    )}
                </ul>
            </nav>
        </header>
    );
};

export default Header;
