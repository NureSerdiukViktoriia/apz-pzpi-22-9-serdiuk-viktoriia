const express = require("express");
const bookingController = require("../controllers/bookingController");
const userController = require("../controllers/userController");
const router = express.Router();
router.post("/",  userController.authorize(["user"]), bookingController.createBooking);
router.get("/", userController.authorize(["admin", "user"]), bookingController.getAllBookings);
router.put("/:id", userController.authorize(["admin"]), bookingController.updateBooking);
router.delete("/:id", userController.authorize(["admin"]), bookingController.deleteBooking);
module.exports = router;
