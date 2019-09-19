import { CRUDRepository } from "../../data-layer/repositories"

export class UserManager {

    private crudRepository: CRUDRepository

    constructor({ crudRepository }) {
        this.crudRepository = crudRepository("User")
    }

    async getUsers() {
        return await this.crudRepository.getAll()
    }

    async getUser(id: any) {
        return await this.crudRepository.getByPk("User", id)
    }

    async createUser(userData: any) {
        return await this.crudRepository.create("User", userData)
    }

}