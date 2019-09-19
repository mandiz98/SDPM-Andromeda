const express = require("express")
const app = express()
const { scopePerRequest, loadControllers } = require('awilix-express')

import * as repositories from "./data-layer/repositories"
import * as managers from "./business-layer/managers"
import { asClass, createContainer } from "awilix"
import sequelize from "./sequelize"

const container = createContainer()
container.register({ 
    userRepository: asClass(repositories.UserRepository).scoped(),

    // UserManager
    userManager: asClass(managers.UserManager).scoped()
 })

app.use(scopePerRequest(container))
app.use(loadControllers('presentation-layer/routes/*.js', { cwd: __dirname }))


app.listen(8080, function() {
    console.log("API listening on port 8080")
    console.log(sequelize.models)
})