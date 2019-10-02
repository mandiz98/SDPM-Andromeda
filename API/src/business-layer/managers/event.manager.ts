import { UserRepository, CRUDRepository } from "../../data-layer/repositories"
import Event from "../../data-layer/models/event.model"

export class EventManager {

    private crudRepository: CRUDRepository

    constructor({ crudRepository }) {
        this.crudRepository = crudRepository("Event")
    }

    async getEvents(query: any = null) {
        return await this.crudRepository.getAll(query)
    }

    async getEvent(id: any) {
        return await this.crudRepository.getByPk(id)
    }

    async createEvent(eventData: Event) {
        return await this.crudRepository.create(eventData)
    }
}