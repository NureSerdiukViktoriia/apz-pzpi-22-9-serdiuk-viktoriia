import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Home.css";
import Footer from "./Footer";
import { useTranslation } from "react-i18next";

const Home = () => {
  const { t } = useTranslation();
  const role = localStorage.getItem("role");
  const token = localStorage.getItem("token");
  const [locations, setLocations] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [imageFile, setImageFile] = useState(null);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [imageError, setImageError] = useState("");
  const [validationErrors, setValidationErrors] = useState({});

  const [newLocation, setNewLocation] = useState({
    name: "",
    type: "",
    price: "",
    description: "",
    max_capacity: "",
  });
  const [editingLocationId, setEditingLocationId] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    fetchLocations();
  }, [token]);
  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/registration";
  };
  const handleEditProfile = () => {
    navigate("/profile");
  };
  const handleBookingPanel = () => {
    navigate("/admin/bookings");
  };
  const validate = (location) => {
    const errors = {};

    if (!location.name || !/^[а-щА-ЩЬьЮюЯяЇїІіЄєҐґA-Za-z\s'-]+$/.test(location.name)) {
      errors.name = t("home.cannotContainNumbers");
    }

    if (!location.type || !/^[а-щА-ЩЬьЮюЯяЇїІіЄєҐґA-Za-z\s'-]+$/.test(location.type)) {
      errors.type = t("home.cannotContainNumbers");
    }
    return errors;
  };

  const handleDeleteLocation = async (locationId) => {
    try {
      await axios.delete(`http://localhost:5000/api/locations/${locationId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      fetchLocations();
    } catch (error) {
      console.error(
        "Помилка при видаленні локації:",
        error.response ? error.response.data : error.message
      );
    }
  };

  const fetchLocations = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/locations", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setLocations(response.data);
    } catch (error) {
      console.error("Помилка при завантаженні локацій:", error);
    }
  };

  const handleAddLocation = async (e) => {
    e.preventDefault();
    const errors = validate(newLocation);
    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);
      return;
    }
    const price = parseFloat(newLocation.price);
    const max_capacity = parseInt(newLocation.max_capacity, 10);

    if (isNaN(price) || isNaN(max_capacity)) {
      console.error("Ціна або максимальна кількість людей неправильні");
      return;
    }

    const formData = new FormData();
    formData.append("name", newLocation.name);
    formData.append("type", newLocation.type);
    formData.append("price", price);
    formData.append("description", newLocation.description);
    formData.append("max_capacity", max_capacity);
    formData.append("availability", false);
    if (imageFile) {
      formData.append("image", imageFile);
    } else {
      setImageError("Зображення обов’язкове для створення локації");
      return;
    }
    setImageError("");

    try {
      await axios.post("http://localhost:5000/api/locations", formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });
      setShowModal(false);
      setNewLocation({
        name: "",
        type: "",
        price: "",
        description: "",
        max_capacity: "",
      });
      setImageFile(null);
      fetchLocations();
    } catch (error) {
      console.error(
        "Помилка при додаванні локації:",
        error.response ? error.response.data : error.message
      );
    }
  };

  const handleEditLocation = async (e) => {
    e.preventDefault();
    const errors = validate(newLocation);
    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);
      return;
    }

    const price = parseFloat(newLocation.price);
    const max_capacity = parseInt(newLocation.max_capacity, 10);

    if (isNaN(price) || isNaN(max_capacity)) {
      console.error("Ціна або максимальна кількість людей неправильні");
      return;
    }

    const formData = new FormData();
    formData.append("name", newLocation.name);
    formData.append("type", newLocation.type);
    formData.append("price", price);
    formData.append("description", newLocation.description);
    formData.append("max_capacity", max_capacity);
    if (imageFile) {
      formData.append("image", imageFile);
    }

    try {
      await axios.put(
        `http://localhost:5000/api/locations/${editingLocationId}`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data",
          },
        }
      );
      setShowModal(false);
      setEditingLocationId(null);
      setNewLocation({
        name: "",
        type: "",
        price: "",
        description: "",
        max_capacity: "",
      });
      setImageFile(null);
      fetchLocations();
    } catch (error) {
      console.error(
        "Помилка при редагуванні локації:",
        error.response ? error.response.data : error.message
      );
    }
  };

  const handleEditClick = (location) => {
    setNewLocation({
      name: location.name,
      type: location.type,
      price: location.price.toString(),
      description: location.description,
      max_capacity: location.max_capacity.toString(),
      image: location.image || null,
    });
    setImageFile(null);
    setEditingLocationId(location.id);
    setShowModal(true);
  };

  return (
    <div className="home-container">
      <div className="home-user">
        <img
          src="/img/user.png"
          alt=""
          className="user-img"
          onClick={() => setShowUserMenu(!showUserMenu)}
        />
        {showUserMenu && (
          <div className="dropdown-menu">
            <button onClick={handleEditProfile}>{t("home.profile")}</button>
            {role === "admin" && (
              <button onClick={handleBookingPanel}>{t("home.booking")}</button>
            )}
            <button onClick={handleLogout}>{t("home.logOut")}</button>
          </div>
        )}
      </div>
      <div className="home-header">
        <img src="/img/logo.webp" alt="logo" className="main-logo" />
        <h1>{t("home.title")}</h1>
        <p className="container-main-p">{t("home.description")}</p>
        <div className="container-main">
          <img src="/img/first.webp" alt="" />
          <img src="/img/second.webp" alt="" />
          <img src="/img/third.webp" alt="" />
        </div>
        {role === "admin" && (
          <button
            className="add-btn"
            onClick={() => {
              setEditingLocationId(null);
              setNewLocation({
                name: "",
                type: "",
                price: "",
                description: "",
                max_capacity: "",
              });
              setImageFile(null);
              setShowModal(true);
            }}
          >
            {t("home.addLocation")}
          </button>
        )}
      </div>
      <p className="all-camings">{t("home.allCampings")} </p>
      <div className="locations-grid">
        {locations.map((location) => (
          <div key={location.id} className="location-card">
            {location.image && (
              <img
                src={location.image}
                alt={location.name}
                className="location-image"
              />
            )}
            <h3>{location.name}</h3>
            <p>
              {t("home.price")} {location.price}
              {t("home.uahPerDay")}{" "}
            </p>

            {role === "admin" && (
              <div className="admin-actions">
                <button onClick={() => handleEditClick(location)}>
                  {t("home.edit")}
                </button>
                <button onClick={() => handleDeleteLocation(location.id)}>
                  {t("home.delete")}
                </button>
              </div>
            )}
            <button
              className="details-button"
              onClick={() =>
                navigate(`/locations/${location.id}`, { state: { location } })
              }
            >
              {t("home.details")}
            </button>
          </div>
        ))}
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>
              {editingLocationId
                ? t("home.editLocation")
                : t("home.createLocation")}
            </h2>

            <form
              className="location-form"
              onSubmit={
                editingLocationId !== null
                  ? handleEditLocation
                  : handleAddLocation
              }
            >
              <input
                type="text"
                placeholder={t("home.namePlaceholder")}
                value={newLocation.name}
                onChange={(e) => {
                  setNewLocation({ ...newLocation, name: e.target.value });
                  setValidationErrors({ ...validationErrors, name: "" });
                }}
                required
              />
              {validationErrors.name && (
                <p className="error-text">{validationErrors.name}</p>
              )}
              <input
                type="text"
                placeholder={t("home.typePlaceholder")}
                value={newLocation.type}
                onChange={(e) => {
                  setNewLocation({ ...newLocation, type: e.target.value });
                  setValidationErrors({ ...validationErrors, type: "" });
                }}
                required
              />
              {validationErrors.type && (
                <p className="error-text">{validationErrors.type}</p>
              )}
              <input
                type="number"
                placeholder={t("home.pricePlaceholder")}
                value={newLocation.price}
                min="1"
                onChange={(e) =>
                  setNewLocation({ ...newLocation, price: e.target.value })
                }
                required
              />
              <textarea
                placeholder={t("home.descriptionPlaceholder")}
                value={newLocation.description}
                onChange={(e) =>
                  setNewLocation({
                    ...newLocation,
                    description: e.target.value,
                  })
                }
                required
              />
              <input
                type="number"
                placeholder={t("home.maxCapacityOfPeople")}
                value={newLocation.max_capacity}
                min="1"
                onChange={(e) =>
                  setNewLocation({
                    ...newLocation,
                    max_capacity: e.target.value,
                  })
                }
                required
              />
              <label className="file-upload-label">
                <span>
                  {t("home.addImage")} / {t("home.editImage")}
                </span>

                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => {
                    setImageFile(e.target.files[0]);
                    setImageError("");
                  }}
                  style={{ display: "none" }}
                />
              </label>
              {imageError && <p className="error-text">{imageError}</p>}
              {validationErrors.form && (
                <p className="error-text">{validationErrors.form}</p>
              )}
              {imageFile && (
                <>
                  <p
                    style={{
                      marginTop: "5px",
                      fontSize: "14px",
                      color: "#555",
                    }}
                  >
                    {t("home.chosenFile")} {imageFile.name}
                  </p>
                  <div style={{ marginTop: "10px" }}>
                    <img
                      src={URL.createObjectURL(imageFile)}
                      alt={imageFile.name}
                      style={{ maxWidth: "100%", maxHeight: "150px" }}
                    />
                  </div>
                </>
              )}

              {!imageFile && newLocation.image && (
                <div style={{ marginTop: "10px" }}>
                  <img
                    src={newLocation.image}
                    alt="Поточне зображення"
                    style={{ maxWidth: "100%", maxHeight: "150px" }}
                  />
                </div>
              )}

              <div className="modal-buttons">
                <button type="submit">{t("home.save")}</button>
              </div>
            </form>
          </div>
        </div>
      )}
      <Footer />
    </div>
  );
};

export default Home;
