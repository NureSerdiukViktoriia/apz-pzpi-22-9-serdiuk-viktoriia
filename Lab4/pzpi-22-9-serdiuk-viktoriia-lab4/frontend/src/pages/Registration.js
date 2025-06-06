import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import "./Registration.css";
import Footer from "./Footer";
import { useTranslation } from "react-i18next";

const RegistrationPage = () => {
  const { t } = useTranslation();
  const [formData, setFormData] = useState({
    first_name: "",
    last_name: "",
    email: "",
    phone_number: "",
    password: "",
    role: "user",
  });

  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const nameRegex = /^[A-Za-zА-Яа-яЁёІіЇїЄєҐґ\s'-]+$/;
    const phoneRegex = /^[0-9]{10,15}$/;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!nameRegex.test(formData.first_name)) {
      setError(t("register.onlyLettersFirstName"));
      return;
    }

    if (!nameRegex.test(formData.last_name)) {
      setError(t("register.onlyLettersLastName"));
      return;
    }

    if (!phoneRegex.test(formData.phone_number)) {
      setError(t("register.onlyDigitsPhone"));
      return;
    }

    if (!emailRegex.test(formData.email)) {
      setError(t("register.invalidEmail"));
      return;
    }

    if (
      !formData.first_name.trim() ||
      !formData.last_name.trim() ||
      !formData.email.trim() ||
      !formData.phone_number.trim() ||
      !formData.password.trim()
    ) {
      setError(t("register.fillInAllFields"));
      return;
    }

    try {
      const response = await axios.post(
        "http://localhost:3000/api/users",
        formData
      );
      if (response.status === 201) {
        navigate("/login");
      }
    } catch (err) {
      setError(t("register.registrationError") + err.response.data.message);
    }
  };

  return (
    <div className="register-page-wrapper">
      <div className="register-container">
        <img src="/img/logo.webp" alt="" className="logo-register" />
        <h2>{t("register.registration")}</h2>
        <form onSubmit={handleSubmit}>
          <div>
            <label>{t("profile.firstName")}</label>
            <input
              type="text"
              name="first_name"
              value={formData.first_name}
              onChange={handleInputChange}
              required
            />
          </div>
          <div>
            <label>{t("profile.secondName")}</label>
            <input
              type="text"
              name="last_name"
              value={formData.last_name}
              onChange={handleInputChange}
              required
            />
          </div>
          <div>
            <label>{t("register.email")}</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              required
            />
          </div>
          <div>
            <label>{t("register.phoneNumber")}</label>
            <input
              type="text"
              name="phone_number"
              value={formData.phone_number}
              onChange={handleInputChange}
              required
            />
          </div>
          <div>
            <label>{t("login.password")}</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleInputChange}
              required
            />
          </div>

          <div>
            <label>{t("register.role")}</label>
            <select
              name="role"
              value={formData.role}
              onChange={handleInputChange}
              required
            >
              <option value="user">{t("register.user")}</option>
              <option value="admin">{t("register.admin")}</option>
            </select>
          </div>

          {error && <p style={{ color: "red" }}>{error}</p>}
          <button type="submit">{t("login.register")}</button>
        </form>
        <p className="registration-link-to-login">
          {t("register.haveAccount")}{" "}
          <Link to="/login">{t("login.enter")}</Link>
        </p>
      </div>
      <Footer />
    </div>
  );
};

export default RegistrationPage;
