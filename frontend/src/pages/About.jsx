import React from "react";

// 이미지 import (src/assets 하위에 있을 때)
import cropImg from "../assets/images/crop.jpg";
import soilImg from "../assets/images/soil.jpg";
import weatherImg from "../assets/images/weather.jpg";
import productionImg from "../assets/images/production.jpg";

const cards = [
    {
        img: cropImg,
        alt: "작물 관리",
        title: "작물 관리",
        desc: "작물 상태 등록 및 최적의 솔루션 제공"
    },
    {
        img: soilImg,
        alt: "토양 분석",
        title: "알림 기능",
        desc: "등록 작물에 대한 최적의 성장환경 제공"
    },
    {
        img: weatherImg,
        alt: "기상 정보",
        title: "기상 정보",
        desc: "실시간 기상 정보를 제공하여 작물 보호"
    },
    {
        img: productionImg,
        alt: "농업 소식",
        title: "농업 소식",
        desc: "농업, 기후, 환경 등의 관련 소식 제공"
    }
];

export default function Intro() {
    return (
        <div className="min-h-screen">
            <div className="container mx-auto px-4">
                <main className="flex flex-col items-center justify-center text-center p-10 mt-52 text-white">
                    <h2 className="text-5xl font-extrabold text-gray-100 drop-shadow-lg">스마트 농업 관리 솔루션</h2>
                    <p className="text-lg text-gray-300 mt-6 italic">
                        "효율적이고 스마트한 농업 관리를 통해 최고의 생산성을 제공합니다."
                    </p>

                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 mt-12">
                        {cards.map((card, idx) => (
                            <div
                                key={idx}
                                className="relative w-64 h-96 rounded-2xl overflow-hidden shadow-2xl hover:transition-transform transform hover:scale-105"
                            >
                                <img
                                    src={card.img}
                                    alt={card.alt}
                                    className="w-full h-full object-cover"
                                    loading="lazy"
                                />
                                <div className="absolute inset-0 bg-gradient-to-t from-black/80 to-transparent"></div>
                                <div className="absolute bottom-4 left-4 text-left">
                                    <h3 className="text-2xl font-semibold text-gray-100 drop-shadow-md">
                                        {card.title}
                                    </h3>
                                    <p className="text-white text-sm drop-shadow-md mt-2">{card.desc}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </main>
                <div className="py-32"></div>
            </div>
        </div>
    );
}