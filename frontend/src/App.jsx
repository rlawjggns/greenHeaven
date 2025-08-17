import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import News from "./pages/News.jsx";
import Board from "./pages/Board.jsx";
import About from "./pages/About.jsx";
import Weather from "./pages/Weather.jsx";
import Crop from "./pages/Crop.jsx";
import Crops from "./pages/Crops.jsx";
import Cropregistration from "./pages/CropRegistration.jsx";
import Growth from "./pages/Growth.jsx";
import Notifications from "./pages/Notifications";
import FindPwd from "./pages/FindPwd";
import PostCreate from "./pages/PostCreate.jsx";
import PostUpdate from "./pages/PostUpdate.jsx";
import Post from "./pages/Post.jsx";
import Profile from "./pages/Profile.jsx";
// 필요한 페이지 import (예시: Login, Signup 등 추가 가능)

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Home />} />s
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<Signup />} />
                <Route path="/news" element={<News />} />
                <Route path="/board" element={<Board />} />
                <Route path="/about" element={<About />} />
                <Route path="/weather" element={<Weather />} />
                <Route path="/crop" element={<Crop />} />
                <Route path="/crops" element={<Crops />} />
                <Route path="/notifications" element={<Notifications />} />
                <Route path="/crop/registration" element={<Cropregistration />} />
                <Route path="/login/password/find" element={<FindPwd />} />
                <Route path="/crop/growth" element={<Growth />} />
                <Route path="/posts/create" element={<PostCreate />} />
                <Route path="/posts/:id" element={<Post />} />
                <Route path="/posts/:id/update" element={<PostUpdate />} />
                <Route path="/profile" element={<Profile />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;