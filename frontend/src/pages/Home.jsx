// frontend/src/pages/Home.jsx
import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

function Home() {
    return (
        <div className="container mx-auto px-4">
            <Header />
            <main className="flex flex-col items-start justify-center h-screen text-center relative z-10 pl-14 text-white">
                <div className="mb-10">
                    <ul className="text-left space-y-2">
                        <li>
                            <strong className="text-lime-600 text-2xl">작</strong>
                            물 관리: 농작물을 기록하고 수확일을 관리합니다.
                        </li>
                        <li>
                            <strong className="text-lime-600 text-2xl">생</strong>
                            장 상태: 특이 사항을 반영하고 현 상태에 최적화합니다.
                        </li>
                        <li>
                            <strong className="text-lime-600 text-2xl">기</strong>
                            상 정보: 실시간 기상 정보를 제공하여 작물을 보호합니다.
                        </li>
                    </ul>
                </div>
                <p className="text-xl">
                    "효율적이고 스마트한 농업 관리를 통해 최고의 생산성을 제공합니다."
                </p>
            </main>
            <Footer />
        </div>
    );
}

export default Home;