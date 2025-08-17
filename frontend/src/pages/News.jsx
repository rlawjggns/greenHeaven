// src/pages/News.jsx
import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

// 예시용 뉴스 데이터 (props나 API로 대체 가능)
const dummyNewsList = [
  {
    title: "농업 뉴스 1",
    description: "농업 관련 주요 소식입니다.",
    link: "https://www.nong-up-news-1.com",
  },
  {
    title: "농업 뉴스 2",
    description: "농업에 관한 두 번째 소식입니다.",
    link: "https://www.nong-up-news-2.com",
  },
  {
    title: "농업 뉴스 3",
    description: "농업 정책과 트렌드 소개.",
    link: "https://www.nong-up-news-3.com",
  },
  {
    title: "농업 뉴스 4",
    description: "농가 지원 정책 안내.",
    link: "https://www.nong-up-news-4.com",
  },
];

export default function News({ newsList = dummyNewsList }) {
  return (
    <div className="text-white min-h-screen">
      <div className="container mx-auto px-4">
        <Header />
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
              <img
                src={`/images/news_${(index % 4) + 1}.jpg`}
                alt="뉴스 이미지"
                className="w-full h-72 object-cover"
                loading="lazy"
              />
              <div className="p-6">
                <h3 className="text-2xl font-semibold text-gray-800">{news.title}</h3>
                <p className="text-gray-600 mt-4">{news.description}</p>
                <a
                  href={news.link}
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
        <Footer />
      </div>
    </div>
  );
}