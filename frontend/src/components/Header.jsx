import React, { useContext, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import headerLogoImg from "../assets/images/logo.png";
import { AuthContext } from "../context/AuthContext";
import { EventSourcePolyfill } from 'event-source-polyfill';

const Header = () => {
    const { isAuthenticated, logout } = useContext(AuthContext);
    const navigate = useNavigate();
    const [hasNewNotification, setHasNewNotification] = useState(false);

    useEffect(() => {
        let eventSource;
        if (isAuthenticated) {
            const token = localStorage.getItem("jwtToken");
            if (token) {
                const sseUrl = `${import.meta.env.VITE_API_URL}${import.meta.env.VITE_API_DEFAULT_PREFIX}/notifications/connect`;
                eventSource = new EventSourcePolyfill(sseUrl, {
                    headers: { 'Authorization': `Bearer ${token}` },
                    heartbeatTimeout: 60 * 60 * 1000,
                });

                eventSource.addEventListener('notification', (event) => {
                    console.log("새 알림 수신:", event.data);
                    setHasNewNotification(true);
                });

                eventSource.addEventListener('dummy', (event) => {
                    console.log("SSE 연결 성공:", event.data);
                });

                eventSource.onerror = (error) => {
                    console.error("SSE 연결 오류:", error);
                };
            }
        }
        return () => {
            if (eventSource) {
                eventSource.close();
            }
        };
    }, [isAuthenticated]);

    const handleLogout = (e) => {
        e.preventDefault();
        logout();
        navigate("/");
    };

    const handleProtectedNav = (e, path) => {
        e.preventDefault();
        if (!isAuthenticated) {
            navigate("/signin");
        } else {
            navigate(path);
        }
    };

    const handleDashboardNav = (e) => {
        e.preventDefault();
        if (!isAuthenticated) {
            navigate("/signin");
        } else {
            setHasNewNotification(false);
            navigate("/dashboard");
        }
    }

    return (
        <header className="flex justify-between items-center pt-4 z-50 fixed top-0 left-0 w-full bg-transparent">
            {/* 로고 */}
            <div className="flex items-center ml-10">
                <Link to="/" className="block">
                    <img src={headerLogoImg} alt="Logo" className="h-32 mr-5" />
                </Link>
            </div>

            <nav className="flex items-center justify-between w-full mr-10">
                <ul className="flex space-x-5 ml-auto">
                    {/* ... (다른 메뉴 동일) ... */}
                    <li>
                        <a href="/about" onClick={(e) => handleProtectedNav(e, "/about")} className="text-gray-300 hover:text-gray-400 transition duration-500">
                            기능소개
                        </a>
                    </li>
                    <li className="relative">
                        <a href="/dashboard" onClick={handleDashboardNav} className="relative inline-block text-gray-300 hover:text-gray-400 transition duration-500">
                            작물관리
                            {hasNewNotification && (
                                <span className="absolute top-0 right-0 block w-2.5 h-2.5 bg-red-500 rounded-full transform translate-x-2/3 -translate-y-1/2"></span>
                            )}
                        </a>
                    </li>
                    <li>
                        <a href="/weather" onClick={(e) => handleProtectedNav(e, "/weather")} className="text-gray-300 hover:text-gray-400 transition duration-500">
                            기상정보
                        </a>
                    </li>
                    <li>
                        <a href="/news" onClick={(e) => handleProtectedNav(e, "/news")} className="text-gray-300 hover:text-gray-400 transition duration-500">
                            농업소식
                        </a>
                    </li>
                    <li>
                        <a href="/posts" onClick={(e) => handleProtectedNav(e, "/posts")} className="text-gray-300 hover:text-gray-400 transition duration-500">
                            소통마당
                        </a>
                    </li>

                    {/* ... (로그인/프로필/로그아웃 부분 동일) ... */}
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
                                <a href="/logout" className="text-gray-300 hover:text-gray-400 transition duration-500" onClick={handleLogout}>
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