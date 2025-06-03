import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import "./Profile.css";
import Footer from "./Footer";
import { useTranslation } from "react-i18next";

const Profile = () => {
  const { t } = useTranslation();
  const token = localStorage.getItem("token");
  const navigate = useNavigate();
  const [error, setError] = useState("");

  const decodedToken = token ? jwtDecode(token) : null;
  const userId = decodedToken ? decodedToken.id : null;

  const [userData, setUserData] = useState({
    first_name: "",
    last_name: "",
    email: "",
    phone_number: "",
  });
  const [editMode, setEditMode] = useState(false);

  useEffect(() => {
    if (!userId) {
      navigate("/registration");
      return;
    }
    fetchUserData();
  }, [userId]);
  const handleDeleteAccount = async () => {
    try {
      await axios.delete(`http://localhost:5000/api/users/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      localStorage.removeItem("token");
      navigate("/registration");
    } catch (error) {
      console.error("Помилка при видаленні акаунта:", error);
    }
  };

  const fetchUserData = async () => {
    try {
      const response = await axios.get(
        `http://localhost:5000/api/users/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setUserData(response.data);
    } catch (error) {
      if (error.response && error.response.status === 401) {
        navigate("/registration");
      }
    }
  };

  const handleChange = (e) => {
    setUserData({ ...userData, [e.target.name]: e.target.value });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setError("");

    const nameRegex = /^[A-Za-zА-Яа-яЇїІіЄєҐґ\s'-]+$/;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneRegex = /^[+]?[0-9]{10,15}$/;

    if (
      !nameRegex.test(userData.first_name) ||
      !nameRegex.test(userData.last_name)
    ) {
      setError(t("profile.nameSurnameLettersOnly"));
      return;
    }

    if (!emailRegex.test(userData.email)) {
      setError(t("profile.invalidEmail"));
      return;
    }

    if (!phoneRegex.test(userData.phone_number)) {
      setError(t("profile.invalidPhone"));
      return;
    }

    try {
      await axios.put(`http://localhost:5000/api/users/${userId}`, userData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setEditMode(false);
      fetchUserData();
    } catch (error) {
      setError(t("profile.updateError"));
    }
  };

  return (
    <div className="profile">
      <button className="back-button" onClick={() => navigate("/home")}>
        <img src="/img/arrow.png" alt="Назад" />
      </button>
      <div className="profile-container">
        <h1>{t("profile.profile")}</h1>
        {!editMode ? (
          <div className="profile-view">
            <p>
              <b>{t("profile.firstName")}</b> {userData.first_name}
            </p>
            <p>
              <b>{t("profile.secondName")}</b> {userData.last_name}
            </p>
            <p>
              <b>{t("profile.email")}</b> {userData.email}
            </p>
            <p>
              <b>{t("profile.phone")}</b> {userData.phone_number}
            </p>
            <button onClick={() => setEditMode(true)}>{t("home.edit")}</button>
          </div>
        ) : (
          <form onSubmit={handleSave}>
            {error && <p style={{ color: "red" }}>{error}</p>}

            <input
              type="text"
              name="first_name"
              value={userData.first_name}
              onChange={handleChange}
              placeholder={t("profile.namePlaceholder")}
              required
            />
            <input
              type="text"
              name="last_name"
              value={userData.last_name}
              onChange={handleChange}
              placeholder={t("profile.surnamePlaceholder")}
              required
            />
            <input
              type="email"
              name="email"
              value={userData.email}
              onChange={handleChange}
              placeholder="Email"
              required
            />
            <input
              type="tel"
              name="phone_number"
              value={userData.phone_number}
              onChange={handleChange}
              placeholder={t("profile.phonePlaceholder")}
              pattern="^[+]?[0-9]{10,15}$"
              required
            />
            <div>
              <button type="submit">{t("home.save")}</button>
              <button type="button" onClick={() => setEditMode(false)}>
                {t("profile.cancel")}
              </button>
            </div>
          </form>
        )}
        <button onClick={handleDeleteAccount}>
          {t("profile.deleteAccount")}
        </button>
      </div>
      <Footer />
    </div>
  );
};

export default Profile;
