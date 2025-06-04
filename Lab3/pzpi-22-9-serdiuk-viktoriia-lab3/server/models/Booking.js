const { DataTypes } = require("sequelize");
const sequelize = require("../db");

const Booking = sequelize.define("Booking", {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
  },
  start_date: {
    type: DataTypes.DATE,
    allowNull: false,
  },
  end_date: {
    type: DataTypes.DATE,
    allowNull: false,
  },
  total_price: {
    type: DataTypes.FLOAT,
    allowNull: true,
  },
  payment_status: {
    type: DataTypes.TEXT,
    allowNull: false,
    validate: {
      isIn: [['paid', 'pending', 'failed']],
    },
  },
  payment_date: {
    type: DataTypes.DATE,
    allowNull: true,
  },
});

Booking.associate = (models) => {
  Booking.belongsTo(models.User, {
    foreignKey: {
      name: "user_id",
      allowNull: false,
    },
    onDelete: "CASCADE",
  });
  Booking.belongsTo(models.Location, {
    foreignKey: {
      name: "location_id",
      allowNull: false,
    },
    onDelete: "CASCADE",
  });
};

module.exports = Booking;
