// frontend/src/components/Header.jsx
import React, { useEffect, useRef, useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import headerLogoImg from "../assets/images/logo.png";

// 편의를 위해 로그인 상태를 전역 state나 props로 받는다고 가정합니다.
// 실제로는 Context, Redux, 전역 상태관리 혹은 상위 App 컴포넌트에서 내려주세요.
const isAuthenticated = false; // TODO: 실제 로그인 상태와 연동

const STORAGE_KEY = "crop-notification-unread";

const Header = () => {
  const [showBadge, setShowBadge] = useState(false);
  const badgeRef = useRef(null);
  const location = useLocation();
  const navigate = useNavigate();

  // 알림 SSE 연결 및 상태 관리
  useEffect(() => {
    if (isAuthenticated) {
      let eventSource;
      // 최초 1번만 연결
      if (!window.eventSourceInitialized) {
        window.eventSourceInitialized = true;
        eventSource = new window.EventSource("/notification/connect");

        eventSource.addEventListener("notification", (event) => {
          // 새 알림 도착
          setShowBadge(true);
          sessionStorage.setItem(STORAGE_KEY, "true");
        });

        eventSource.onerror = (error) => {
          console.error("SSE 연결 오류:", error);
          eventSource.close();
        };
      }

      // 첫 마운트 시 sessionStorage 검사
      if (sessionStorage.getItem(STORAGE_KEY)) {
        setShowBadge(true);
      }

      // 언마운트시 EventSource 끊기
      return () => {
        if (eventSource) eventSource.close();
      };
    }
    // eslint-disable-next-line
  }, [isAuthenticated]);

  // "작물관리" 화면에 들어왔을 때 알림 읽음처리
  useEffect(() => {
    if (location.pathname.startsWith("/crop") && isAuthenticated) {
      fetch("/notification/mark-read", { method: "POST" })
        .then((response) => {
          if (response.ok) {
            setShowBadge(false);
            sessionStorage.removeItem(STORAGE_KEY);
          }
        })
        .catch((err) => console.error("읽음 처리 API 오류:", err));
    }
  }, [location.pathname]);

  return (
    <header className="flex justify-between items-center pt-4 z-50 fixed top-0 left-0 w-full bg-transparent">
      {/* 로고 영역 */}
      <div className="flex items-center ml-10">
        <Link to="/" className="block">
          <img src={headerLogoImg} alt="Logo" className="h-32 mr-5" />
        </Link>
      </div>

      {/* 네비게이션 메뉴 */}
      <nav className="flex items-center justify-between w-full mr-10">
        <ul className="flex space-x-5 ml-auto">
          <li>
            <Link
              to="/about"
              className="text-gray-300 hover:text-gray-400 transition duration-500"
            >
              기능소개
            </Link>
          </li>
          <li className="relative">
            <Link
              to="/crop"
              className="text-gray-300 hover:text-gray-400 transition duration-500"
            >
              작물관리
            </Link>
            <span
              ref={badgeRef}
              className={
                "absolute left-0 top-full mt-1 px-2 py-0.5 text-xs bg-red-500 text-white rounded whitespace-nowrap shadow-lg z-10" +
                (showBadge ? "" : " hidden")
              }
              id="crop-notification-badge"
            >
              아직 읽지 않은 알림이 있어요!
            </span>
          </li>
          <li>
            <Link
              to="/weather"
              className="text-gray-300 hover:text-gray-400 transition duration-500"
            >
              기상정보
            </Link>
          </li>
          <li>
            <Link
              to="/news"
              className="text-gray-300 hover:text-gray-400 transition duration-500"
            >
              농업소식
            </Link>
          </li>
          <li>
            <Link
              to="/board"
              className="text-gray-300 hover:text-gray-400 transition duration-500"
            >
              소통마당
            </Link>
          </li>
          {!isAuthenticated && (
            <li>
              <Link
                to="/login"
                className="text-gray-300 hover:text-gray-400 transition duration-500"
              >
                로그인
              </Link>
            </li>
          )}
          {isAuthenticated && (
            <>
              <li>
                <Link
                  to="/member/profile"
                  className="text-gray-300 hover:text-gray-400 transition duration-500"
                >
                  프로필
                </Link>
              </li>
              <li>
                <a
                  href="/member/logout"
                  className="text-gray-300 hover:text-gray-400 transition duration-500"
                  onClick={(e) => {
                    e.preventDefault();
                    // 실제 로그아웃 기능 구현 필요(예: API 호출 등)
                    // 예제용이므로 홈으로 이동만
                    navigate("/");
                  }}
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
