const { DataTypes } = require("sequelize");
const sequelize = require("../db");

const Resource = sequelize.define("Resource", {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
  },
  type: {
    type: DataTypes.TEXT("electricity", "water"),
    allowNull: false,
    validate: {
      isIn: [['electricity', 'water']],
    },
  },
  consumption: {
    type: DataTypes.FLOAT,
    allowNull: false,
    validate: {
      min: 0,
    },
  },
  resource_limit: {
    type: DataTypes.FLOAT,
    allowNull: false,
    validate: {
      min: 0,
    },
  },
});

Resource.associate = (models) => {
  Resource.belongsTo(models.Location, {
    foreignKey: {
      name: "location_id",
      allowNull: false,
    },
    onDelete: "CASCADE",
  });
};

module.exports = Resource;
