const { DataTypes } = require("sequelize");
const sequelize = require("../db");

const Review = sequelize.define("Review", {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
  },
  date: {
    type: DataTypes.DATE,
    allowNull: false,
  },
  rating: {
    type: DataTypes.FLOAT,
    allowNull: false,
    validate: {
      min: 1,
      max: 5,
    },
  },
  message: {
    type: DataTypes.TEXT,
    allowNull: false,
  },
});

Review.associate = (models) => {
  Review.belongsTo(models.User, {
    foreignKey: {
      name: "user_id",
      allowNull: false,
    },
    onDelete: "CASCADE",
  });
  Review.belongsTo(models.Location, {
    foreignKey: {
      name: "location_id",
      allowNull: false,
    },
    onDelete: "CASCADE",
  });
};

module.exports = Review;
