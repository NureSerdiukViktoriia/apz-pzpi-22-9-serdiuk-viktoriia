const express = require("express");
const app = express();
const port = 5000;
const cors = require("cors");
const bodyParser = require("body-parser");
const sequelize = require("./db");
const Booking = require("./models/Booking");
const Resource = require("./models/Resource");
const Review = require("./models/Review");
const User = require("./models/User");
const Location = require("./models/Location");
app.use(cors({ origin: "*" }));

app.use(bodyParser.json());
Booking.associate(sequelize.models);
Resource.associate(sequelize.models);
Review.associate(sequelize.models);
Location.associate(sequelize.models);

sequelize
  .sync({ force: false })
  .then(() => {
    console.log("Synced db!");
  })
  .catch((error) => {
    console.log("Failed to sync db:", error.message);
  });

const userRoutes = require("./routes/userRoutes");
app.use("/api/users", userRoutes);
const locationRoutes = require("./routes/locationRoutes");
app.use("/api/locations", locationRoutes);
const reviewRoutes = require("./routes/reviewRoutes");
app.use("/api/reviews", reviewRoutes);
const resourceRoutes = require("./routes/resourceRoutes");
app.use("/api/resources", resourceRoutes);
const bookingRoutes = require("./routes/bookingRoutes");
app.use("/api/bookings", bookingRoutes);
const login = require("./routes/userRoutes");
app.use("/api", login);
const getWater = require("./routes/resourceRoutes");
app.use("/api/resources", getWater);
const getElectricity = require("./routes/resourceRoutes");
app.use("/api/resources", getElectricity);
app.use("/uploads", express.static("uploads"));

app.get("/", (req, res) => {
  res.send("Camping System");
});
app.listen(port, () => {
// app.listen(port, "0.0.0.0", () => {
  console.log(`Server is running on port ${port}`);
});
