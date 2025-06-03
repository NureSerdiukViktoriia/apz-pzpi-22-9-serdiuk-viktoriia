import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import "./Login.css";
import Footer from "./Footer";
import { useTranslation } from "react-i18next";

const LoginPage = () => {
  const { t } = useTranslation();
  const [credentials, setCredentials] = useState({
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setCredentials((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!credentials.email || !credentials.password) {
      setError(t("login.fillInAllFields"));
      return;
    }

    if (!emailRegex.test(credentials.email)) {
      setError(t("login.invalidEmail"));
      return;
    }

    try {
      const response = await axios.post(
        "http://localhost:5000/api/login",
        credentials
      );
      const { token } = response.data;

      localStorage.setItem("token", token);

      const decoded = jwtDecode(token);
      localStorage.setItem("role", decoded.role);
      navigate("/home");
    } catch (err) {
      setError(t("login.invalidCredentials"));
    }
  };

  return (
    <div className="login-page-wrapper">
      <div className="login-container">
        <img src="/img/logo.webp" alt="" className="logo-register" />
        <h2> {t("login.login")}</h2>
        <form onSubmit={handleSubmit}>
          <div>
            <label> {t("login.email")}</label>
            <input
              type="email"
              name="email"
              value={credentials.email}
              onChange={handleInputChange}
              required
            />
          </div>
          <div>
            <label>{t("login.password")}</label>
            <input
              type="password"
              name="password"
              value={credentials.password}
              onChange={handleInputChange}
              required
            />
          </div>

          {error && <p style={{ color: "red" }}>{error}</p>}
          <button type="submit">{t("login.enter")}</button>
        </form>
        <p className="registration-link-to-login">
          {t("login.noAccount")}{" "}
          <Link to="/registration">{t("login.register")}</Link>
        </p>
      </div>
      <Footer />
    </div>
  );
};

export default LoginPage;
