// frontend/src/context/AuthContext.jsx
import React, { createContext, useState, useEffect } from "react";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    // 초기 로드 시 localStorage에 JWT 있는지 확인
    useEffect(() => {
        const token = localStorage.getItem("jwtToken");
        if (token) setIsAuthenticated(true);
    }, []);

    const login = (token) => {
        localStorage.setItem("jwtToken", token);
        setIsAuthenticated(true);
    };

    const logout = () => {
        localStorage.removeItem("jwtToken");
        setIsAuthenticated(false);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
