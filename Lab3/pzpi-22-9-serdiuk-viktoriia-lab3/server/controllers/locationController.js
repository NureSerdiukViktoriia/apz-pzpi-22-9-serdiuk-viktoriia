const Location = require("../models/Location");
const Booking = require("../models/Booking");
const Review = require("../models/Review");


const createLocation = async (req, res) => {
  try {
    req.body.availability = true;
    if (req.file) {
      req.body.image = `/uploads/${req.file.filename}`;
    }
    const newLocation = await Location.create(req.body);
    return res
      .status(201)
      .json({ message: "Location created!", location: newLocation });
  } catch (error) {
  if (error.name === "SequelizeValidationError" || error.name === "SequelizeUniqueConstraintError") {
    return res.status(400).json({ error: error.errors.map(e => e.message) });
  }
  return res.status(500).send(error.message);
  }
};

const getAllLocations = async (req, res) => {
  try {
    const locations = await Location.findAll({});
    if (locations && locations.length > 0) {
      return res.status(200).json(locations);
    }
    throw new Error("Locations do not exist");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

const getLocationById = async (req, res) => {
  const id = req.params.id;

  try {
    const location = await Location.findByPk(id, {
      include: [
        { model: Booking, as: "Bookings" },
        { model: Review, as: "Reviews" }
      ],
    });

    if (!location) {
      return res.status(404).json({ message: "Location not found" });
    }

    res.json(location);
  } catch (error) {
    console.error("Error fetching location:", error);
    res.status(500).json({ message: "Server error" });
  }
};

const updateLocation = async (req, res) => {
  try {
    const { id } = req.params;
    const updateData = { ...req.body };
    if (req.file) {
      updateData.image = `/uploads/${req.file.filename}`;
    }

    const [updated] = await Location.update(updateData, {
      where: { id: id },
    });
    if (updated) {
      const updatedLocation = await Location.findOne({ where: { id: id } });
      return res
        .status(200)
        .json({ message: "Location updated!", location: updatedLocation });
    }
    throw new Error("Location not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};
const deleteLocation = async (req, res) => {
  try {
    const { id } = req.params;
    const deleted = await Location.destroy({
      where: { id: id },
    });
    if (deleted) {
      return res.status(204).json();
    }
    throw new Error("Location not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

module.exports = {
  createLocation,
  getAllLocations,
  updateLocation,
  deleteLocation,
  getLocationById,
};
