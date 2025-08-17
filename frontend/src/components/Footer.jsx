// frontend/src/components/Footer.jsx
import React from "react";
import footerLogoImg from "../assets/images/logo.png";
import {faEnvelope, faLocation, faMobileScreen, faSquareRss} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faGithub} from "@fortawesome/free-brands-svg-icons";

// 폰트어썸 아이콘 사용을 위해 @fortawesome/react-fontawesome 등 라이브러리 설치하거나 CDN으로 불러와야 합니다.
// 여기서는 className은 유지하며, 실제 아이콘 사용 방법은 주석 참고.

const Footer = () => (
  <footer>
    {/* 배경을 전체 너비로 확장 */}
    <div className="bg-black text-gray-200 py-20 w-full">
      <div className="max-w-screen-xl mx-auto px-4">
        <div className="flex justify-between text-sm">
          {/* 로고 및 저작권 */}
          <div>
            <img
              src={footerLogoImg}
              alt="Logo"
              className="mb-4 h-52"
            />
            <p className="text-gray-100 text-xs">COPYRIGHTⓒGREENHEAVEN</p>
          </div>
          {/* 회사 정보 */}
          <div>
            <p className="text-gray-100 pb-3 font-bold">COMPANY</p>
            <p className="text-gray-400">그린헤븐(GREENHEAVEN)</p>
            <p className="text-gray-400">대표: 김정훈</p>
            <p className="text-gray-400">사업자등록번호: 123-45-67890</p>
            <p className="text-gray-400">통신판매업신고: 2025-통신판매업-1234호</p>
            <p className="text-gray-400 pt-3">Terms Of Use | Privacy</p>
          </div>
          {/* 연락처 및 정보 */}
          <div>
            <p className="text-gray-100 pb-3 font-bold">CONTACT</p>
            <p className="text-gray-100 pb-3 font-bold">
                <FontAwesomeIcon icon={faMobileScreen} />
                <span className="ml-2">
                    010-3379-1271
                </span>
            </p>
            <p className="text-gray-400 pb-9">
              AM9 - PM6 (주말, 공휴일 휴무)
            </p>
            <p className="text-gray-100 pb-3">
                <FontAwesomeIcon icon={faEnvelope} />
                <span className="ml-2">
                    rlawjggns12@gmail.com
                </span>
            </p>
            <p className="text-gray-100 pb-3">
                <FontAwesomeIcon icon={faLocation} />
                <span className="ml-2">
                    대구시 북구 태전로314 KOP타워 302호
                </span>
            </p>
          </div>
          {/* 은행 정보 및 소셜 링크 */}
          <div>
            <p className="text-gray-100 pb-3 font-bold">BANK INFO</p>
            <p className="text-gray-100 pb-9">
              대구은행 09113132635 김정훈
            </p>
            <p className="text-gray-100 pb-3 font-bold">FOLLOW ME</p>
            {/* Follow Me 항목들을 수직으로 정렬 */}
            <div className="flex flex-col space-y-3">
              <a href="#" className="text-gray-100">
                  <FontAwesomeIcon icon={faGithub} />
                  <span className="ml-2">
                    Github
                  </span>
              </a>
              <a href="#" className="text-gray-100">
                <FontAwesomeIcon icon={faSquareRss} />
                  <span className="ml-2">
                    Velog
                  </span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>
);

export default Footer;
