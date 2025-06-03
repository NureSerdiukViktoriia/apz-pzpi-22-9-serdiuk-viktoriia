const { Sequelize } = require("sequelize");

const sequelize = new Sequelize({
  dialect: "sqlite",
  storage: "./camping.db",
  logging: false,
});

const connection = async () => {
  try {
    await sequelize.authenticate();
    console.log("Connection has been established successfully!");
  } catch (error) {
    console.error("Unable to connect to the database:", error);
  }
};

connection();

module.exports = sequelize;
