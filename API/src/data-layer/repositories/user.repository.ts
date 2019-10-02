import { CRUDRepository } from "./crud.repository"
import User from "../models/user.model"
import sequelize from "../../sequelize"

export class UserRepository extends CRUDRepository {

    constructor() {
        super({}, "User")
    }

    async clockIn(primaryKey: number) {
        return await this.update(primaryKey, { clockedIn: true })
    }

    async clockOut(primaryKey: number) {
        return await this.update(primaryKey, { clockedIn: false })
    }

    async getByRfid(rfid: string) {
        return await User.findOne({
            where: {
                rfid: rfid
            }
        })
    }
}