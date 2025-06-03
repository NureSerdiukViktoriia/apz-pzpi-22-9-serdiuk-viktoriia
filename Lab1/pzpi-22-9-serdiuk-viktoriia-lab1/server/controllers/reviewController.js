const Review = require("../models/Review");

const createReview = async (req, res) => {
  try {
    const newReview = await Review.create(req.body);
    return res
      .status(201)
      .json({ message: "Review created!", location: newReview });
  } catch (error) {
    return res.status(500).send(error.message);
  }
};
// const getReviewsByLocationId = async (req, res) => {
//   try {
//     const { id } = req.params;
//     const reviews = await Review.findAll({
//       where: { location_id: id },
//       order: [["createdAt", "DESC"]],
//     });

//     if (reviews.length > 0) {
//       return res.status(200).json(reviews);
//     }

//     return res
//       .status(404)
//       .json({ message: "Немає відгуків для цієї локації." });
//   } catch (error) {
//     return res.status(500).send(error.message);
//   }
// };
const getReviewsByLocationId = async (req, res) => {
  try {
    const { id } = req.params;
    const reviews = await Review.findAll({
      where: { location_id: id },
      order: [["createdAt", "DESC"]],
    });
    return res.status(200).json(reviews);
  } catch (error) {
    return res.status(500).send(error.message);
  }
};


const getAllReviews = async (req, res) => {
  try {
    const reviews = await Review.findAll({});
    if (reviews && reviews.length > 0) {
      return res.status(200).json(reviews);
    }
    throw new Error("Reviews do not exist");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};
const updateReview = async (req, res) => {
  try {
    const { id } = req.params;
    const [updated] = await Review.update(req.body, {
      where: { id: id },
    });
    if (updated) {
      const updatedReview = await Review.findOne({ where: { id: id } });
      return res
        .status(200)
        .json({ message: "Review updated!", review: updatedReview });
    }
    throw new Error("Review not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};
const deleteReview = async (req, res) => {
  try {
    const { id } = req.params;
    const deleted = await Review.destroy({
      where: { id: id },
    });
    if (deleted) {
      return res.status(204).json();
    }
    throw new Error("Review not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

module.exports = {
  createReview,
  getAllReviews,
  updateReview,
  deleteReview,
  getReviewsByLocationId,
};
