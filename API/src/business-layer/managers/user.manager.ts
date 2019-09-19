import { UserRepository, CRUDRepository } from "../../data-layer/repositories"

export class UserManager {

    private userRepository: UserRepository
    private crudRepository: CRUDRepository

    constructor({ userRepository, crudRepository }) {
        this.userRepository = userRepository
        this.crudRepository = crudRepository
    }

    async getUsers() {
        return await this.crudRepository.getAll("User")
    }

    async getUser(id: any) {
        return await this.crudRepository.getByPk("User", id)
    }

    async createUser(userData: any) {
        return await this.crudRepository.create("User", userData)
    }

}