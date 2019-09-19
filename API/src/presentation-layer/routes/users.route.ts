import bodyParser from "body-parser"
import { route, GET, POST, before } from "awilix-express"
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
            res.status(400).json({ status: 400, message: "Invalid body blabla", name: "Bad Request" })
        }
    }
}