const express = require("express")
const bodyParser = require("body-parser")
const app = express()
const { scopePerRequest, loadControllers } = require('awilix-express')

import * as repositories from "./data-layer/repositories"
import * as managers from "./business-layer/managers"
import { asClass, createContainer, asFunction } from "awilix"
import sequelize from "./sequelize"

const makeCrudRepo = (container) => {
    return (data): repositories.CRUDRepository => {
        return new repositories.CRUDRepository(container, data)
    }
}
const container = createContainer()
container.register({ 
    // Repositories
    crudRepository: asFunction(makeCrudRepo),
    userRepository: asClass(repositories.UserRepository),
    // Managers
    userManager: asClass(managers.UserManager).scoped(),
    eventManager: asClass(managers.EventManager).scoped(),
})
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))

 app.use(scopePerRequest(container))
app.use(loadControllers('presentation-layer/routes/*.js', { cwd: __dirname }))


app.listen(process.env.PORT || 8080, function() {
    console.log("API listening on port " + (process.env.PORT ? process.env.PORT : 8080))
    console.log(app)
})