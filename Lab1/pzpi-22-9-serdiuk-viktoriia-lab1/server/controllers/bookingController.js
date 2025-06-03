const Booking = require("../models/Booking");
const Location = require("../models/Location");
const User = require("../models/User");

const createBooking = async (req, res) => {
  try {
    const { start_date, end_date, location_id } = req.body;
    if (new Date(end_date) < new Date(start_date)) {
      return res
        .status(400)
        .json({ message: "End date cannot be earlier than start date." });
    }
    const location = await Location.findOne({ where: { id: location_id } });
    if (!location || !location.availability) {
      return res
        .status(400)
        .json({ message: "Location is not available for booking." });
    }
    const start = new Date(start_date);
    const end = new Date(end_date);
    const duration = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
    const totalPrice = duration * location.price;

    const newBooking = await Booking.create({
      user_id: req.user.id,
      location_id,
      start_date,
      end_date,
      total_price: totalPrice,
      payment_status: "pending",
      payment_date: start_date,
    });
    await Location.update(
      { availability: false },
      { where: { id: location_id } }
    );
    return res
      .status(201)
      .json({ message: "Booking created!", booking: newBooking });
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

const getAllBookings = async (req, res) => {
  try {
    const bookings = await Booking.findAll({
      include: [
        {
          model: User,
          attributes: [
            "id",
            "first_name",
            "last_name",
            "email",
            "phone_number",
          ],
        },
        { model: Location, attributes: ["id", "name", "price"] },
      ],
    });

    return res.status(200).json(bookings);
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

const updateBooking = async (req, res) => {
  try {
    const { id } = req.params;
    const { start_date, end_date, location_id } = req.body;

    if (new Date(end_date) < new Date(start_date)) {
      return res
        .status(400)
        .json({ message: "End date cannot be earlier than start date." });
    }

    let location = null;
    if (location_id) {
      location = await Location.findOne({ where: { id: location_id } });
      if (!location || !location.availability) {
        return res
          .status(400)
          .json({ message: "Location is not available for booking." });
      }
    }

    if (!location) {
      const existingBooking = await Booking.findOne({ where: { id } });
      if (!existingBooking) {
        return res.status(404).json({ message: "Booking not found" });
      }
      location = await Location.findOne({
        where: { id: existingBooking.location_id },
      });
    }

    const start = new Date(start_date);
    const end = new Date(end_date);
    const duration = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
    const totalPrice = duration * location.price;

    const [updated] = await Booking.update(
      {
        ...req.body,
        total_price: totalPrice,
      },
      { where: { id } }
    );

    if (updated) {
      const updatedBooking = await Booking.findOne({ where: { id } });
      return res
        .status(200)
        .json({ message: "Booking updated!", booking: updatedBooking });
    }

    throw new Error("Booking not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

const deleteBooking = async (req, res) => {
  try {
    const { id } = req.params;
    const deleted = await Booking.destroy({
      where: { id: id },
    });
    if (deleted) {
      return res.status(204).json();
    }
    throw new Error("Booking not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

module.exports = {
  createBooking,
  getAllBookings,
  updateBooking,
  deleteBooking,
};
