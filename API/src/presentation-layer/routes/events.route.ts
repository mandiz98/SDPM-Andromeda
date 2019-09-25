import { route, GET, POST, before, PUT } from "awilix-express"
import { UserManager, EventManager } from "../../business-layer/managers"


@route("/events")
export default class EventRoutes {
        
    private eventManager: EventManager

    constructor({ eventManager }) {
        this.eventManager = eventManager
    }

    @route("/")
    @GET()
    async getEvents(req, res) {
        try {
            res.json(await this.eventManager.getEvents(req.query))
        }catch(error) {
            console.log(error)
            res.status(500).json(error)
        }
    }

    @route("/:id")
    @GET()
    async getEvent(req, res) {
        try {
            res.json(await this.eventManager.getEvent(req.params.id))
        }catch(error) {
            console.log(error)
            res.status(500).json(error)
        }
    }

    @route("/")
    @POST()
    async createEvent(req, res) {
        try {
            res.json(await this.eventManager.createEvent(req.body))
        }catch(error) {
            res.status(500).json({ status: 500, message: error, name: error.name })
        }
    }
}