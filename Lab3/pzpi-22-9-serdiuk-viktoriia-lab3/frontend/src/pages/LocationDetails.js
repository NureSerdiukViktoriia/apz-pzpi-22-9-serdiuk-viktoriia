import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import "./LocationDetails.css";
import Footer from "./Footer";
import { useTranslation } from "react-i18next";
const LocationDetails = () => {
  const { t } = useTranslation();
  const { id } = useParams();
  const token = localStorage.getItem("token");
  const decodedToken = token ? jwtDecode(token) : null;
  const userId = decodedToken ? decodedToken.id : null;
  const role = localStorage.getItem("role");
  const [location, setLocation] = useState(null);
  const [showBookingForm, setShowBookingForm] = useState(false);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [bookingError, setBookingError] = useState("");
  const [bookingSuccess, setBookingSuccess] = useState("");
  const navigate = useNavigate();
  const [rating, setRating] = useState(0);
  const [message, setMessage] = useState("");
  const [reviewError, setReviewError] = useState("");
  const [reviewSuccess, setReviewSuccess] = useState("");
  const [reviews, setReviews] = useState([]);
  const [editReviewId, setEditReviewId] = useState(null);
  const [editRating, setEditRating] = useState(0);
  const [editMessage, setEditMessage] = useState("");
  const [editError, setEditError] = useState("");
  const [editSuccess, setEditSuccess] = useState("");

  useEffect(() => {
    fetchLocation();
    fetchReviews();
  }, [id, token]);

  const fetchLocation = async () => {
    try {
      const response = await axios.get(
        `http://localhost:5000/api/locations/${id}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setLocation(response.data);
    } catch (error) {
      console.error("Помилка при завантаженні локації:", error);
    }
  };

  const fetchReviews = async () => {
    try {
      const response = await axios.get(
        `http://localhost:5000/api/reviews/location/${id}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setReviews(response.data);
    } catch (error) {
      console.error("Помилка при завантаженні відгуків:", error);
    }
  };

  const handleBookingSubmit = async (e) => {
    e.preventDefault();
    setBookingError("");
    setBookingSuccess("");

    if (!startDate || !endDate) {
      setBookingError("Будь ласка, заповніть обидві дати.");
      return;
    }
    if (new Date(startDate) >= new Date(endDate)) {
      setBookingError("Дата початку має бути раніше дати закінчення.");
      return;
    }

    try {
      const diffTime = Math.abs(new Date(endDate) - new Date(startDate));
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      const totalPrice = diffDays * location.price;

      await axios.post(
        "http://localhost:5000/api/bookings",
        {
          location_id: id,
          start_date: startDate,
          end_date: endDate,
          total_price: totalPrice,
          payment_status: "pending",
          payment_date: startDate,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setBookingSuccess(t("locationDetails.successfulBooking"));

      setShowBookingForm(false);
    } catch (error) {
      console.error("Помилка при створенні бронювання:", error);
      setBookingError(t("locationDetails.errorBooking"));
    }
  };

  const handleEditReviewSubmit = async (e) => {
    e.preventDefault();
    setEditError("");
    setEditSuccess("");

    if (!editRating || !editMessage) {
      setEditError(t("footer.enterAllFields"));
      return;
    }

    try {
      await axios.put(
        `http://localhost:5000/api/reviews/${editReviewId}`,
        {
          rating: editRating,
          message: editMessage,
          date: new Date().toISOString(),
          user_id: userId,
          location_id: id,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setEditSuccess(t("locationDetails.reviewUpdated"));
      setEditReviewId(null);
      setEditRating(0);
      setEditMessage("");
      fetchReviews();
    } catch (err) {
      console.error("Помилка оновлення відгуку:", err);
      setEditError(t("locationDetails.errorReviewUpdated"));
    }
  };

  const handleDeleteReview = async (reviewId) => {
    try {
      await axios.delete(`http://localhost:5000/api/reviews/${reviewId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchReviews();
    } catch (err) {
      console.error("Помилка видалення відгуку:", err);
    }
  };

  if (!location) return <div>Завантаження...</div>;

  return (
    <div className="location-details">
      <button className="back-button" onClick={() => navigate("/home")}>
        <img src="/img/arrow.png" alt="Назад" />
      </button>

      <div className="location-details-container">
        <h2>{location.name}</h2>
        <img src={location.image} alt={location.name} />

        <p>
          {t("locationDetails.type")} {location.type}
        </p>
        <p>
          {t("home.price")} {location.price} {t("home.uahPerDay")}
        </p>
        <p>
          {t("locationDetails.description")} {location.description}
        </p>

        {role === "user" && !showBookingForm && (
          <button onClick={() => setShowBookingForm(true)}>
            {t("locationDetails.toBook")}
          </button>
        )}

        {showBookingForm && (
          <form onSubmit={handleBookingSubmit}>
            <div>
              <label>{t("locationDetails.startDate")}</label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                required
              />
            </div>
            <div>
              <label>{t("locationDetails.endDate")}</label>
              <input
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                required
              />
            </div>
            <button type="submit">{t("locationDetails.confirmBooking")}</button>
            <button type="button" onClick={() => setShowBookingForm(false)}>
              {t("locationDetails.cancel")}
            </button>
            {bookingError && <p style={{ color: "red" }}>{bookingError}</p>}
            {bookingSuccess && (
              <p style={{ color: "green" }}>{bookingSuccess}</p>
            )}
          </form>
        )}

        {role === "admin" ? (
          <p></p>
        ) : (
          <>
            <h3 className="location-details-all-reviews">
              {t("locationDetails.allReviews")}
            </h3>
            {reviews.length === 0 ? (
              <p>{t("locationDetails.noReviews")}</p>
            ) : (
              <ul>
                {reviews.map((review) =>
                  editReviewId === review.id ? (
                    <li key={review.id}>
                      <form onSubmit={handleEditReviewSubmit}>
                        <label>
                          {t("locationDetails.rating")}
                          <select
                            value={editRating}
                            onChange={(e) =>
                              setEditRating(Number(e.target.value))
                            }
                            required
                          >
                            {[1, 2, 3, 4, 5].map((n) => (
                              <option key={n} value={n}>
                                {n}
                              </option>
                            ))}
                          </select>
                        </label>
                        <br />
                        <label>
                          {t("locationDetails.comment")}
                          <textarea
                            value={editMessage}
                            onChange={(e) => setEditMessage(e.target.value)}
                            required
                          />
                        </label>
                        <br />
                        <button type="submit">{t("home.save")}</button>
                        <button
                          type="button"
                          onClick={() => {
                            setEditReviewId(null);
                            setEditError("");
                            setEditSuccess("");
                          }}
                        >
                          {t("locationDetails.cancel")}
                        </button>
                        {editError && (
                          <p style={{ color: "red" }}>{editError}</p>
                        )}
                        {editSuccess && (
                          <p style={{ color: "green" }}>{editSuccess}</p>
                        )}
                      </form>
                    </li>
                  ) : (
                    <li key={review.id}>
                      <strong>{t("locationDetails.rating")}</strong>{" "}
                      {review.rating}/5
                      <br />
                      <strong>{t("locationDetails.comment")}</strong>{" "}
                      {review.message}
                      {role === "user" && userId === review.user_id && (
                        <div>
                          <button
                            onClick={() => {
                              setEditReviewId(review.id);
                              setEditRating(review.rating);
                              setEditMessage(review.message);
                              setEditError("");
                              setEditSuccess("");
                            }}
                          >
                            {t("home.edit")}
                          </button>
                          <button onClick={() => handleDeleteReview(review.id)}>
                            {t("home.delete")}
                          </button>
                        </div>
                      )}
                    </li>
                  )
                )}
              </ul>
            )}
            <hr></hr>
            {role === "user" && editReviewId === null && (
              <form
                onSubmit={async (e) => {
                  e.preventDefault();
                  setReviewError("");
                  setReviewSuccess("");

                  if (!rating || !message) {
                    setReviewError(t("locationDetails.fillInAllFields"));
                    return;
                  }

                  try {
                    await axios.post(
                      "http://localhost:5000/api/reviews",
                      {
                        location_id: id,
                        rating,
                        message,
                        date: new Date().toISOString(),
                        user_id: userId,
                      },
                      {
                        headers: { Authorization: `Bearer ${token}` },
                      }
                    );
                    setReviewSuccess(t("locationDetails.addedReview"));
                    setRating(0);
                    setMessage("");
                    fetchReviews();
                  } catch (err) {
                    setReviewError(t("locationDetails.NoReview"));
                  }
                }}
              >
                <h4>{t("locationDetails.addReview")}</h4>
                <label>
                  {t("locationDetails.rating")}
                  <select
                    value={rating}
                    onChange={(e) => setRating(Number(e.target.value))}
                    required
                  >
                    <option value={0} disabled>
                      {t("locationDetails.chooseRating")}
                    </option>
                    {[1, 2, 3, 4, 5].map((n) => (
                      <option key={n} value={n}>
                        {n}
                      </option>
                    ))}
                  </select>
                </label>
                <br />
                <label>
                  {t("locationDetails.comment")}
                  <textarea
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    required
                  />
                </label>
                <br />
                <button type="submit">{t("locationDetails.sendReview")}</button>
                {reviewError && <p style={{ color: "red" }}>{reviewError}</p>}
                {reviewSuccess && (
                  <p style={{ color: "green" }}>{reviewSuccess}</p>
                )}
              </form>
            )}
          </>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default LocationDetails;
