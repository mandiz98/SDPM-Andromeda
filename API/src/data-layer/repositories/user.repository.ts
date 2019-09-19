import User from "../models/user.model";

export class UserRepository {

    async getUsers() {
        return await User.findAll()
    }

    async getUser(id: number) {
        return await User.findByPk(id)
    }

}