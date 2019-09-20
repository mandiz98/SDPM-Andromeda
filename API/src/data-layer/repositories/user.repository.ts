import { CRUDRepository } from "./crud.repository";

export class UserRepository extends CRUDRepository {

    private crudRepository: CRUDRepository

    constructor({ crudRepository }) {
        super({}, "User")
    }

    /**
     * TODO: Should create an event that stores CLOCKED_IN
     * @param primaryKey number
     */
    async clockIn(primaryKey: number) {
        return await this.update(primaryKey, { clockedIn: true })
    }

    /**
     * TODO: Should create an event that stores CLOCKED_IN
     * @param primaryKey number
     */
    async clockedOut(primaryKey: number) {
        return await this.update(primaryKey, { clockedIn: false })
    }
}