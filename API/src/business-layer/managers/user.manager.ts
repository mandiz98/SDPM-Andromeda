import { UserRepository } from "../../data-layer/repositories"

export class UserManager {

    private userRepository: UserRepository

    constructor({ userRepository }) {
        this.userRepository = userRepository
    }

    async getUsers() {
        return await this.userRepository.getUsers()
    }

    async getUser(id: any) {
        return await this.userRepository.getUser(id)
    }

}