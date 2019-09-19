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
            console.log(error);
            res.json(error)
        }
    }

    @route("/:id")
    @GET()
    async getUser(req, res) {
        try {
            res.json(await this.userManager.getUser(req.params.id))
        }catch(error) {
            console.log(error);
            res.json(error)
        }
    }
}