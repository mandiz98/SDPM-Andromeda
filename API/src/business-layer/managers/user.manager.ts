import { UserRepository } from "../../data-layer/repositories"

export class UserManager {

    private userRepository: UserRepository

    constructor({ userRepository }) {
        this.userRepository = userRepository
    }

    async getUsers() {
        return await this.userRepository.getAll()
    }

    async getUser(id: any) {
        return await this.userRepository.getByPk(id)
    }

    async createUser(userData: any) {
        return await this.userRepository.create(userData)
    }

    async clockInOut(id: any, clockIn: boolean) {
        return clockIn ? await this.userRepository.clockIn(id) : await this.userRepository.clockOut(id)
    }

}