import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import SignIn from "./pages/SignIn";
import Signup from "./pages/Signup";
import News from "./pages/News.jsx";
import Posts from "./pages/Posts.jsx";
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
import { AuthProvider } from "./context/AuthContext";
import ScrollToTop from "./components/ScrollToTop";

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <ScrollToTop />
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/signin" element={<SignIn />} />
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/news" element={<News />} />
                    <Route path="/posts" element={<Posts />} />
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
        </AuthProvider>
    );
}

export default App;