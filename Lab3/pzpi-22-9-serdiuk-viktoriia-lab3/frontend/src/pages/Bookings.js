import React, { useEffect, useState } from "react";
import axios from "axios";
import "./Bookings.css";
import { useNavigate } from "react-router-dom";
import Footer from "./Footer";
import { useTranslation } from "react-i18next";

const Bookings = () => {
  const { t } = useTranslation();
  const [bookings, setBookings] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [editDates, setEditDates] = useState({ start_date: "", end_date: "" });
  const navigate = useNavigate();
  useEffect(() => {
    fetchBookings();
  }, []);
  const handleHomePage = () => {
    navigate("/home");
  };
  const fetchBookings = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get("/api/bookings", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setBookings(response.data);
    } catch (error) {
      console.error("Помилка при отриманні бронювань:", error);
    }
  };

  const handleDelete = async (id, locationId) => {
    try {
      const token = localStorage.getItem("token");

      await axios.delete(`/api/bookings/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      await axios.put(
        `/api/locations/${locationId}`,
        { availability: true },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setBookings(bookings.filter((b) => b.id !== id));
    } catch (error) {
      console.error(
        "Помилка при видаленні бронювання або оновленні доступності:",
        error
      );
    }
  };

  const startEditing = (booking) => {
    setEditingId(booking.id);
    setEditDates({
      start_date: booking.start_date.slice(0, 10),
      end_date: booking.end_date.slice(0, 10),
    });
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditDates({ start_date: "", end_date: "" });
  };

  const handleDateChange = (e) => {
    const { name, value } = e.target;
    setEditDates((prev) => ({ ...prev, [name]: value }));
  };

  const saveDates = async (id) => {
    try {
      const token = localStorage.getItem("token");

      const response = await axios.put(
        `/api/bookings/${id}`,
        {
          start_date: editDates.start_date,
          end_date: editDates.end_date,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const updatedBooking = response.data.booking;

      setBookings((prev) =>
        prev.map((b) =>
          b.id === id
            ? {
                ...b,
                start_date: updatedBooking.start_date,
                end_date: updatedBooking.end_date,
                total_price: updatedBooking.total_price,
              }
            : b
        )
      );

      cancelEditing();
    } catch (error) {
      console.error("Помилка при оновленні дат:", error);
    }
  };

  return (
    <div className="bookings">
      <button className="back-button" onClick={() => navigate("/home")}>
        <img src="/img/arrow.png" alt="Назад" />
      </button>
      <div className="bookings-container">
        <h2 className="bookings-title">{t("booking.listOfBookings")}</h2>
        {bookings.length === 0 ? (
          <p className="bookings-empty">{t("booking.noBookings")}</p>
        ) : (
          <ul className="bookings-list">
            {bookings.map((booking) => (
              <li key={booking.id} className="booking-item">
                <p>
                  <strong>{t("booking.usersName")}</strong>{" "}
                  {booking.User
                    ? `${booking.User.first_name} ${booking.User.last_name}`
                    : "Н/д"}
                </p>
                <p>
                  <strong>{t("booking.usersEmail")}</strong>{" "}
                  {booking.User?.email || "Н/д"}
                </p>
                <p>
                  <strong>{t("booking.usersPhoneNumber")}</strong>{" "}
                  {booking.User?.phone_number || "Н/д"}
                </p>
                <p>
                  <strong>{t("booking.location")}</strong>{" "}
                  {booking.Location?.name || "Н/д"}
                </p>

                <p>
                  <strong>{t("booking.startBooking")}</strong>{" "}
                  {editingId === booking.id ? (
                    <input
                      type="date"
                      name="start_date"
                      value={editDates.start_date}
                      onChange={handleDateChange}
                    />
                  ) : (
                    new Date(booking.start_date).toLocaleDateString()
                  )}
                </p>
                <p>
                  <strong>{t("booking.endBooking")}</strong>{" "}
                  {editingId === booking.id ? (
                    <input
                      type="date"
                      name="end_date"
                      value={editDates.end_date}
                      onChange={handleDateChange}
                    />
                  ) : (
                    new Date(booking.end_date).toLocaleDateString()
                  )}
                </p>

                <p>
                  <strong>{t("booking.paymentStatus")}</strong>{" "}
                  {booking.payment_status}
                </p>
                <p>
                  <strong>{t("booking.totalPrice")}</strong>{" "}
                  {booking.total_price ?? "Н/д"}
                </p>

                <div className="booking-buttons">
                  {editingId === booking.id ? (
                    <>
                      <button
                        className="btn btn-update"
                        onClick={() => saveDates(booking.id)}
                      >
                        {t("home.save")}
                      </button>
                      <button
                        className="btn btn-cancel"
                        onClick={cancelEditing}
                      >
                        {t("home.cancel")}
                      </button>
                    </>
                  ) : (
                    <>
                      <button
                        className="btn btn-update"
                        onClick={() => startEditing(booking)}
                      >
                        {t("booking.editDate")}
                      </button>
                      <button
                        className="btn btn-delete"
                        onClick={() =>
                          handleDelete(booking.id, booking.Location.id)
                        }
                      >
                        {t("booking.delete")}
                      </button>
                    </>
                  )}
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default Bookings;
