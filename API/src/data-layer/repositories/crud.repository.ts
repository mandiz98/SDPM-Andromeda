import sequelize from "../../sequelize";

export class CRUDRepository {

    private model: string

    constructor(container = {}, model: string) {
        this.model = model
    }

    async getAll() {
        return await sequelize.model(this.model).findAll()
    }

    async getByPk(model: string, primaryKey: any) {
        return await sequelize.model(model).findByPk(primaryKey)
    }

    async create(model: string, modelData: any) {
        return await sequelize.model(model).build(modelData).save()
    }

    async update(model: string, primaryKey: any, modelData: any) {
        const modelInstance = await this.getByPk(model, primaryKey)
        return await modelInstance.update(modelData)
    }

    async delete(model: string, primaryKey: any) {
        const modelInstance = await this.getByPk(model, primaryKey)
        return await modelInstance.destroy()
    }

}