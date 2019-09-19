import bodyParser from "body-parser"
import { route, GET, POST, before } from "awilix-express"
import { UserRepository } from "../../data-layer/repositories/user.repository"


@route("/users")
export default class UserRoutes {
        
    private userRepository: UserRepository

    constructor({ userRepository }) {
        this.userRepository = userRepository
    }

    @route("/")
    @GET()
    async getUsers(req, res) {
        res.json({})
    }
}