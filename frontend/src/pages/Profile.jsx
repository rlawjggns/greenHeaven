import React, { useEffect, useRef, useState } from "react";
import serverApi from "../utils/serverApi.js";
import QuitModal from "../components/modal/QuitModal.jsx";
import { useNavigate } from "react-router-dom";

export default function Profile() {
    const [form, setForm] = useState({
        oldPassword: "",
        newPassword: "",
        confirmNewPassword: "",
        name: "",
        address: ""
    });
    const [errors, setErrors] = useState({});
    const [quitOpen, setQuitOpen] = useState(false);
    const addressRef = useRef(null);
    const navigate = useNavigate();

    // 페이지 로딩 시 현재 프로필 불러오기
    useEffect(() => {
        serverApi.get("/member/profile")
            .then(res => {
                const { name, address } = res.data;
                setForm(f => ({ ...f, name, address }));
            })
            .catch(err => console.error(err));
    }, []);

    // 주소 찾기
    const handleFindAddress = () => {
        if (!window.daum?.Postcode) {
            const script = document.createElement("script");
            script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
            script.onload = handleFindAddress;
            document.body.appendChild(script);
            return;
        }
        new window.daum.Postcode({
            oncomplete: data => {
                setForm(f => ({ ...f, address: data.roadAddress }));
            }
        }).open();
    };

    // input 값 변경
    const handleChange = e => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    // form 실시간 검증
    useEffect(() => {
        const newErrors = {};

        if (!form.oldPassword.trim()) newErrors.oldPassword = "기존 비밀번호를 입력해주세요.";
        if (!form.name.trim()) newErrors.name = "이름을 입력해주세요.";
        if (!form.address.trim()) newErrors.address = "주소를 입력해주세요.";

        if (form.newPassword || form.confirmNewPassword) {
            if (form.newPassword.length > 0 && form.newPassword.length < 8) {
                newErrors.newPassword = "비밀번호는 최소 8글자 이상이어야 합니다.";
            }
            if (form.newPassword.length > 20) {
                newErrors.newPassword = "비밀번호는 최대 20글자까지 가능합니다.";
            }
            if (form.newPassword !== form.confirmNewPassword) {
                newErrors.confirmNewPassword = "새 비밀번호가 일치하지 않습니다.";
            }
        }

        setErrors(newErrors);
    }, [form]);


// form 제출
    const handleSubmit = async e => {
        e.preventDefault();

        // submit 전에 필수 체크
        const submitErrors = {};
        if (!form.oldPassword.trim()) submitErrors.oldPassword = "기존 비밀번호를 입력해주세요.";
        if (!form.name.trim()) submitErrors.name = "이름을 입력해주세요.";
        if (!form.address.trim()) submitErrors.address = "주소를 입력해주세요.";
        if (form.newPassword && form.newPassword.length < 8) {
            submitErrors.newPassword = "비밀번호는 최소 8글자 이상이어야 합니다.";
        }
        if (form.newPassword && form.newPassword.length > 20) {
            submitErrors.newPassword = "비밀번호는 최대 20글자까지 가능합니다.";
        }
        if (form.newPassword !== form.confirmNewPassword) {
            submitErrors.confirmNewPassword = "새 비밀번호가 일치하지 않습니다.";
        }


        if (Object.keys(submitErrors).length > 0) {
            setErrors(submitErrors);
            return; // 오류 있으면 제출 막기
        }

        try {
            await serverApi.post("/member/profile", form);
            alert("정보가 수정되었습니다.");
            navigate("/");
        } catch (err) {
            console.error(err);
            // 백엔드가 errors 키 없이 단순 객체를 보낼 경우 처리
            if (err.response?.data) {
                setErrors(err.response.data); // <- 여기 수정
            } else {
                alert("정보 수정 중 오류가 발생했습니다.");
            }
        }
    };

    const handleQuit = () => {
        alert("회원탈퇴가 처리되었습니다.");
        setQuitOpen(false);
    };

    return (
        <div className="min-h-screen text-gray-900">
            <QuitModal open={quitOpen} onClose={() => setQuitOpen(false)} onQuit={handleQuit} />
            <div className="container mx-auto px-4">
                <main className="flex items-center justify-center min-h-screen my-48">
                    <section className="bg-white p-8 rounded shadow-lg max-w-md w-full">
                        <form onSubmit={handleSubmit}>
                            <h2 className="flex justify-center text-2xl font-bold mb-6 text-gray-900">프로필 정보 수정</h2>

                            {/* 기존 비밀번호 */}
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">
                                    기존 비밀번호
                                    {errors.oldPassword && <span className="text-red-500 ml-4 text-sm">{errors.oldPassword}</span>}
                                </label>
                                <input type="password" name="oldPassword" value={form.oldPassword} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3"/>
                            </div>

                            {/* 새 비밀번호 */}
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">
                                    새 비밀번호
                                    {errors.newPassword && <span className="text-red-500 ml-4 text-sm">{errors.newPassword}</span>}
                                </label>
                                <input type="password" name="newPassword" value={form.newPassword} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3"/>
                            </div>

                            {/* 비밀번호 확인 */}
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">
                                    비밀번호 확인
                                    {errors.confirmNewPassword && <span className="text-red-500 ml-4 text-sm">{errors.confirmNewPassword}</span>}
                                </label>
                                <input type="password" name="confirmNewPassword" value={form.confirmNewPassword} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3"/>
                            </div>

                            {/* 이름 */}
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">
                                    이름
                                    {errors.name && <span className="text-red-500 ml-4 text-sm">{errors.name}</span>}
                                </label>
                                <input type="text" name="name" value={form.name} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3"/>
                            </div>

                            {/* 주소 */}
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">
                                    주소
                                    {errors.address && <span className="text-red-500 ml-4 text-sm">{errors.address}</span>}
                                </label>
                                <div style={{ display: "flex", gap: 8 }}>
                                    <input ref={addressRef} type="text" name="address" readOnly value={form.address} onChange={handleChange} className="shadow appearance-none border rounded flex-grow py-2 px-3"/>
                                    <button type="button" onClick={handleFindAddress} className="bg-lime-600 hover:bg-lime-800 text-white font-bold py-2 px-4 rounded">주소 찾기</button>
                                </div>
                            </div>

                            <div className="flex justify-center">
                                <button type="submit" className="bg-lime-600 hover:bg-lime-800 text-white font-bold py-2 px-4 rounded">확인</button>
                            </div>

                            <div className="flex justify-end">
                                <button type="button" className="text-red-500 font-bold my-4 hover:text-red-600" onClick={() => setQuitOpen(true)}>회원탈퇴</button>
                            </div>
                        </form>
                    </section>
                </main>
            </div>
        </div>
    );
}
