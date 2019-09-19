const express = require("express")
const app = express()
const { scopePerRequest } = require('awilix-express')

import { asClass, createContainer } from "awilix"

const container = createContainer()
//container.register({  })

app.use(scopePerRequest(container))

app.listen(8080, function() {
    console.log("API listening on port 8080")
})