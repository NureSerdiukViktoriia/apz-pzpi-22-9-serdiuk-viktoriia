import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Main from "./pages/Main";
import Registration from "./pages/Registration";
import Login from "./pages/Login";
import Home from "./pages/Home";
import Profile from "./pages/Profile";
import LocationDetails from "./pages/LocationDetails";
import Bookings from "./pages/Bookings";
import "./i18n";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="/registration" element={<Registration />} />
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<Home />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/locations/:id" element={<LocationDetails />} />
        <Route path="/admin/bookings" element={<Bookings />} />
      </Routes>
    </Router>
  );
};
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
export default App;
