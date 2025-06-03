const Resource = require("../models/Resource");
const Location = require("../models/Location");

const createResource = async (req, res) => {
  try {
    const { consumption, resource_limit } = req.body;
    if (parseFloat(consumption) > parseFloat(resource_limit)) {
      return res
        .status(400)
        .json({ message: "Consumption cannot exceed resource limit" });
    }
    const newResource = await Resource.create(req.body);
    return res
      .status(201)
      .json({ message: "Resource created!", resource: newResource });
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

const getAllResources = async (req, res) => {
  try {
    const resources = await Resource.findAll({});
    if (resources && resources.length > 0) {
      return res.status(200).json(resources);
    }
    throw new Error("Resources do not exist");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};
const updateResource = async (req, res) => {
  try {
    const { id } = req.params;
    const { consumption, resource_limit } = req.body;
    if (parseFloat(consumption) > parseFloat(resource_limit)) {
      return res
        .status(400)
        .json({ message: "Consumption cannot exceed resource limit" });
    }
    const [updated] = await Resource.update(req.body, {
      where: { id: id },
    });
    if (updated) {
      const updatedResource = await Resource.findOne({ where: { id: id } });
      return res
        .status(200)
        .json({ message: "Resource updated!", resource: updatedResource });
    }
    throw new Error("Resource not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};
const deleteResource = async (req, res) => {
  try {
    const { id } = req.params;
    const deleted = await Resource.destroy({
      where: { id: id },
    });
    if (deleted) {
      return res.status(204).json();
    }
    throw new Error("Resource not found");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

const getWater = async (req, res) => {
  await getResources("water", res);
};
const getElectricity = async (req, res) => {
  await getResources("electricity", res);
};

const getResources = async (type, res) => {
  try {
    const resources = await Resource.findAll({
      where: { type },
      include: [
        {
          model: Location,
          attributes: ["name"],
        },
      ],
    });

    if (resources && resources.length > 0) {
      const total_quantity = resources.reduce(
        (sum, resource) => sum + parseFloat(resource.consumption),
        0
      );

      const data = resources.map((resource) => ({
        location_name: resource.Location.name,
        consumption: resource.consumption,
      }));

      return res.status(200).json({
        total_quantity,
        resources: data,
      });
    }
    throw new Error("No data");
  } catch (error) {
    return res.status(500).send(error.message);
  }
};

module.exports = {
  createResource,
  getAllResources,
  updateResource,
  deleteResource,
  getWater,
  getElectricity,
};
