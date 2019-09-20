import { CRUDRepository } from "./crud.repository";

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
}