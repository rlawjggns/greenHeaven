// frontend/src/pages/Home.jsx
import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Header from "../components/Header";
import Footer from "../components/Footer";
import noteImg from "../assets/images/write.jpg";

function Home() {
    const patchNotes = [
        { text: "2025-08-19: UI를 개선하고 및 일부 버그를 수정했어요. 😎", image: noteImg  },
        { text: "2025-08-15: 농작물 기록 기능이 업데이트 되었어요. 확인해보세요! 🗒️", image: noteImg  },
        { text: "2025-08-10: 일부 지역의 기상 정보 정확도를 향상했어요! ✈️", image: noteImg  },
    ];

    const [current, setCurrent] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setCurrent((prev) => (prev + 1) % patchNotes.length);
        }, 3000); // 3초마다 다음 슬라이드
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="container mx-auto px-4">
            <Header />

            <motion.main className="flex flex-col items-start justify-center min-h-screen relative z-10 pl-14 text-white">
                {/* 기존 주요 내용 */}
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

                {/* 패치노트 카드 */}
                <div className="absolute top-3/5 right-6 transform -translate-y-1/2 w-96 h-96 bg-white text-gray-900 p-4 rounded-2xl shadow-xl border border-gray-200 overflow-hidden flex flex-col">
                    {/* 카드 제목 (고정) */}
                    <div className="text-xl font-bold text-gray-800 mb-3 text-center">
                        📢 최근 업데이트
                    </div>

                    {/* 패치노트 슬라이드 */}
                    <div className="relative flex-1">
                        <AnimatePresence>
                            {patchNotes.map((note, index) =>
                                index === current ? (
                                    <motion.div
                                        key={index}
                                        initial={{ x: 100, opacity: 0 }}
                                        animate={{ x: 0, opacity: 1 }}
                                        exit={{ x: -100, opacity: 0 }}
                                        transition={{ duration: 0.5 }}
                                        className="absolute top-0 left-0 w-full h-full flex flex-col"
                                    >
                                        <div className="h-48 overflow-hidden rounded-xl mb-3 shadow-md">
                                            <img
                                                src={note.image}
                                                alt={`Patch ${index}`}
                                                className="w-full h-full object-cover rounded-xl"
                                            />
                                        </div>
                                        <div className="p-3 rounded-lg bg-gray-200 text-gray-800 text-md shadow-inner">
                                            <span className="inline-block w-3 h-3 bg-lime-500 rounded-full mr-2"></span>
                                            {note.text}
                                        </div>
                                    </motion.div>
                                ) : null
                            )}
                        </AnimatePresence>
                    </div>
                </div>

            </motion.main>

            <Footer />
        </div>
    );
}

export default Home;
