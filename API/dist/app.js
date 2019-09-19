"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require("express");
const app = express();
const { scopePerRequest, loadControllers } = require('awilix-express');
const repositories = require("./data-layer/repositories");
const managers = require("./business-layer/managers");
const awilix_1 = require("awilix");
const sequelize_1 = require("./sequelize");
const container = awilix_1.createContainer();
container.register({
    userRepository: awilix_1.asClass(repositories.UserRepository).scoped(),
    // UserManager
    userManager: awilix_1.asClass(managers.UserManager).scoped()
});
app.use(scopePerRequest(container));
app.use(loadControllers('presentation-layer/routes/*.js', { cwd: __dirname }));
app.listen(8080, function () {
    console.log("API listening on port 8080");
    console.log(sequelize_1.default.models);
});
//# sourceMappingURL=app.js.map