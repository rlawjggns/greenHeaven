import React, { useState } from "react";
import CropRegisterationModal from "../components/modal/CropRegistrationModal.jsx";

import lettuceImg from "../assets/images/lettuce.jpg";
import radishImg from "../assets/images/radish.jpg";
import potatoImg from "../assets/images/potato.jpg";
import sweetpotatoImg from "../assets/images/sweetpotato.jpg";

const CROP_CARDS = [
    {
        img: lettuceImg,
        alt: "상추",
        type: "LETTUCE",
        title: "상추",
        desc: "상추는 신선하고 아삭한 잎이 특징인 채소로, 서늘한 기후와 꾸준한 수분 공급이 우수한 품질을 보장해줍니다. 이에 따른 빠른 성장 주기와 관리 용이성이 장점입니다."
    },
    {
        img: radishImg,
        alt: "무",
        type: "RADISH",
        title: "무",
        desc: "무는 아삭한 식감과 은은한 단맛이 어우러진 채소로, 서늘한 날씨와 적절한 물 관리를 통해 우수한 뿌리를 생산합니다. 김치 및 다양한 반찬의 주재료로 활용됩니다."
    },
    {
        img: potatoImg,
        alt: "감자",
        type: "POTATO",
        title: "감자",
        desc: "감자는 영양가가 높고 다양한 요리에 활용되는 구근 작물입니다. 서늘하고 통풍이 잘 되는 토양에서 우수한 수확량을 기대할 수 있으며, 지속적인 병충해 관리가 필수입니다."
    },
    {
        img: sweetpotatoImg,
        alt: "고구마",
        type: "SWEETPOTATO",
        title: "고구마",
        desc: "고구마는 달콤하고 고소한 맛이 매력인 뿌리 작물로, 따뜻한 기후와 배수가 좋은 토양에서 최상의 품질을 발휘합니다. 충분한 일조와 적절한 수분 공급이 건강한 뿌리 발달의 핵심입니다."
    }
];

export default function CropRegisterSelect() {
    const [modalOpen, setModalOpen] = useState(false);
    const [selectedType, setSelectedType] = useState('');

    const handleCardClick = (type) => {
        setSelectedType(type);
        setModalOpen(true);
    };
    const handleCloseModal = () => setModalOpen(false);

    return (
        <div className="text-white min-h-screen relative">
            {/* 모달을 Header 바깥/상위에 항상 위치 */}
            <CropRegisterationModal open={modalOpen} onClose={handleCloseModal} defaultType={selectedType} />

            <div className="container mx-auto px-4">
                <main className="flex flex-col items-center justify-center text-center p-10 mt-60 text-white">
                    <h2 className="text-5xl font-extrabold text-gray-100 drop-shadow-lg">등록할 작물 선택</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 mt-12">
                        {CROP_CARDS.map(card => (
                            <button
                                key={card.type}
                                type="button"
                                className="relative w-64 h-96 rounded-2xl overflow-hidden shadow-2xl transition-transform transform hover:scale-105 focus:outline-none"
                                onClick={() => handleCardClick(card.type)}
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
                            </button>
                        ))}
                    </div>
                </main>
                <div className="py-32"></div>
            </div>
        </div>
    );
}