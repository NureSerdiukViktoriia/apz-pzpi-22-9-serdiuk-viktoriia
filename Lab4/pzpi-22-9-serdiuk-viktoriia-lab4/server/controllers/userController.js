const User = require("../models/User");
const jwt = require("jsonwebtoken");
const bcrypt = require("bcryptjs");

const createUser = async (req, res) => {
  try {
    const existingUser = await User.findOne({ where: { email: req.body.email } });
    if (existingUser) {
      return res.status(409).json({ message: "Користувач із цією електронною поштою вже існує" });
    }
    const newUser = await User.create(req.body);
    return res.status(201).json({ message: "User created!", user: newUser });
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

const getUserById = async (req, res) => {
  try {
    const { id } = req.params;
    const user = await User.findOne({
      where: { id: id },
    });
    if (user) {
      return res.status(200).json(user);
    }
    throw new Error("User with this ID does not exists");
  } catch (error) {
    res.status(500).send(error.message);
  }
};
const updateUser = async (req, res) => {
  try {
    const { id } = req.params;
    const [updated] = await User.update(req.body, {
      where: { id: id },
    });
    if (updated) {
      const updatedUser = await User.findOne({ where: { id: id } });
      return res
        .status(200)
        .json({ message: "User updated!", user: updatedUser });
    }
    throw new Error("User not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};
const deleteUser = async (req, res) => {
  try {
    const { id } = req.params;
    const deleted = await User.destroy({
      where: { id: id },
    });
    if (deleted) {
      return res.status(204).json();
    }
    throw new Error("User not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

const login = async (req, res) => {
  try {
    const { email, password } = req.body;
    const user = await User.findOne({ where: { email } });

    if (!user) {
      return res.status(401).json({ message: "User not found" });
    }

    const isPassword = await bcrypt.compare(password, user.password);
    if (!isPassword) {
      return res.status(401).json({ message: "Incorrect email or password" });
    }

    const token = jwt.sign(
      { id: user.id, role: user.role },
      "H78dre37fo#t1%87!650",
      { expiresIn: "1h" }
    );

    return res.status(200).json({ token });
  } catch (error) {
    console.error("Login error:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

const authorize = (roles) => {
  return (req, res, next) => {
    const token = req.headers.authorization?.split(" ")[1];

    if (!token) {
      return res.status(403).json({ message: "You do not have access" });
    }
    try {
      const decoded = jwt.verify(token, "H78dre37fo#t1%87!650");
      if (!roles.includes(decoded.role)) {
        return res.status(403).json({ message: "You do not have access" });
      }
      req.user = decoded;
      next();
    } catch (error) {
      return res.status(403).send("Invalid token");
    }
  };
};

module.exports = {
  createUser,
  getUserById,
  updateUser,
  deleteUser,
  login,
  authorize,
};
