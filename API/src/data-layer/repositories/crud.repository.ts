import User from "../models/user.model";
import sequelize from "../../sequelize";

export class CRUDRepository {

    async getAll(model: string) {
        return await sequelize.model(model).findAll()
    }

    async getByPk(model: string, pk: any) {
        return await sequelize.model(model).findByPk(pk)
    }

    async create(model: string, modelData: any) {
        return await sequelize.model(model).build(modelData).save()
    }

}