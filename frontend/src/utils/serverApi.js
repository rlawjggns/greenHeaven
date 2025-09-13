import axios from "axios";

const baseURL1 = import.meta.env.VITE_API_URL;
const baseURL2 = import.meta.env.VITE_API_DEFAULT_PREFIX

const serverApi = axios.create({
    baseURL: baseURL1 + baseURL2,
});

// 요청 인터셉터
serverApi.interceptors.request.use(
    config => {
        const token = localStorage.getItem("jwtToken");
        if (token) config.headers.Authorization = `Bearer ${token}`;
        return config;
    },
    error => Promise.reject(error)
);

// 응답 인터셉터
serverApi.interceptors.response.use(
    response => response,
    error => {
        const originalRequest = error.config;

        // 로그인 요청이 아닌 경우에만 401 처리
        if (error.response?.status === 401 && !originalRequest.url.includes("/member/signin")) {
            window.location.href = "/signin";
        }

        return Promise.reject(error);
    }
);


export default serverApi;
