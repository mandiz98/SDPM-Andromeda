import { route, GET, POST, before, PUT } from "awilix-express"
import { UserManager } from "../../business-layer/managers"


@route("/users")
export default class UserRoutes {
        
    private userManager: UserManager

    constructor({ userManager }) {
        this.userManager = userManager
    }

    @route("/")
    @GET()
    async getUsers(req, res) {
        try {
            res.json(await this.userManager.getUsers())
        }catch(error) {
            console.log(error)
            res.status(500).json(error)
        }
    }

    @route("/:id")
    @GET()
    async getUser(req, res) {
        try {
            res.json(await this.userManager.getUser(req.params.id))
        }catch(error) {
            console.log(error)
            res.status(500).json(error)
        }
    }

    @route("/")
    @POST()
    async createUser(req, res) {
        try {
            res.json(await this.userManager.createUser(req.body))
        }catch(error) {
            console.log(error)
            res.status(400).json({ status: 400, message: error, name: "Bad Request" })
        }
    }

    @route("/:id/clock")
    @PUT()
    async clockInOut(req, res) {
        try {
            res.json(await this.userManager.clockInOut(req.params.id, req.body.clockInOut))
        }catch(error) {
            res.status(500).json({ status: 500, message: "Ett fel: " + error, name: "Error" })
        }
    }
}