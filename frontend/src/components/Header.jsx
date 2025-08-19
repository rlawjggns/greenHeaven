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
                        <Link to="/about" className="text-gray-300 hover:text-gray-400 transition duration-500">
                            기능소개
                        </Link>
                    </li>
                    <li>
                        <Link to="/crop" className="text-gray-300 hover:text-gray-400 transition duration-500">
                            작물관리
                        </Link>
                    </li>
                    <li>
                        <Link to="/weather" className="text-gray-300 hover:text-gray-400 transition duration-500">
                            기상정보
                        </Link>
                    </li>
                    <li>
                        <Link to="/news" className="text-gray-300 hover:text-gray-400 transition duration-500">
                            농업소식
                        </Link>
                    </li>
                    <li>
                        <Link to="/board" className="text-gray-300 hover:text-gray-400 transition duration-500">
                            소통마당
                        </Link>
                    </li>

                    {/* 로그인 상태에 따라 표시 */}
                    {!isAuthenticated ? (
                        <li>
                            <Link to="/signin" className="text-gray-300 hover:text-gray-400 transition duration-500">
                                로그인
                            </Link>
                        </li>
                    ) : (
                        <>
                            <li>
                                <Link to="/profile" className="text-gray-300 hover:text-gray-400 transition duration-500">
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
