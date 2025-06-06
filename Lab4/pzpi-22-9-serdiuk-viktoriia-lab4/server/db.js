const { Sequelize } = require("sequelize");

const sequelize = new Sequelize(
  process.env.DB_NAME || "camping_db",
  process.env.DB_USER || "root",
  process.env.DB_PASSWORD || "8135adrpt816wcx_0",
  {
    host: process.env.DB_HOST || "localhost",
    dialect: "mysql",
    logging: false,
  }
);

const connection = async () => {
  try {
    await sequelize.authenticate();
    console.log("MySQL connection established successfully!");
  } catch (error) {
    console.error("Unable to connect to the database:", error);
  }
};

connection();

module.exports = sequelize;
