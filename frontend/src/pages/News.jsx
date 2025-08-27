// src/pages/News.jsx
import React, { useEffect, useState } from "react";
import serverApi from "../utils/serverApi";
import newImg1 from "../assets/images/news_1.jpg";
import newImg2 from "../assets/images/news_2.jpg";
import newImg3 from "../assets/images/news_3.jpg";
import newImg4 from "../assets/images/news_4.jpg";

export default function News() {
    const [newsList, setNewsList] = useState([]);
    const newsImages = [newImg1, newImg2, newImg3, newImg4];


    useEffect(() => {
        serverApi
            .get("/news") // 백엔드 API 호출
            .then((res) => {
                console.log(res)
                setNewsList(res.data);
            })
            .catch((err) => {
                console.error("뉴스 불러오기 실패:", err);
            });
    }, []);

    return (
        <div className="text-white min-h-screen">
            <div className="container mx-auto px-4">
                <div className="mb-16"></div>

                <h2 className="text-2xl font-semibold text-white mb-20 mt-40 text-center">
                    농업 관련 뉴스
                </h2>

                {/* 뉴스 카드 리스트 */}
                <div className="flex flex-col items-center space-y-32">
                    {newsList.map((news, index) => (
                        <div
                            key={index}
                            className="w-3/4 md:w-1/2 lg:w-2/5 relative bg-white rounded-lg overflow-hidden shadow-lg hover:shadow-2xl transform transition duration-500 ease-in-out hover:scale-105 mb-20"
                        >
                            {/* 임시 이미지 (데이터에 이미지가 없다면 index 기반으로 순환) */}
                            <img
                                src={newsImages[index % newsImages.length]} // index 기반 순환
                                alt="뉴스 이미지"
                                className="w-full h-72 object-cover"
                                loading="lazy"
                            />
                            <div className="p-6">
                                <h3 className="text-2xl font-semibold text-gray-800">
                                    {news.title}
                                </h3>
                                <p
                                    className="text-gray-600 mt-4"
                                    dangerouslySetInnerHTML={{ __html: news.description }}
                                />
                                <a
                                    href={news.link || news.originallink}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-lime-600 mt-4 inline-block hover:underline"
                                >
                                    자세히 보기
                                </a>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="py-32"></div>
            </div>
        </div>
    );
}
