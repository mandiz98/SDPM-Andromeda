import sequelize from "../../sequelize";

export class CRUDRepository {

    private model: string

    constructor({}, model: string) {
        this.model = model
    }

    async getAll(query: any = null) {
        return await sequelize.model(this.model).findAll(query ? { where: query } : {})
    }

    async getByPk(primaryKey: any) {
        return await sequelize.model(this.model).findByPk(primaryKey)
    }

    async create(modelData: any) {
        return await sequelize.model(this.model).build(modelData).save()
    }

    async update(primaryKey: any, modelData: any) {
        const modelInstance = await this.getByPk(primaryKey)
        return await modelInstance.update(modelData)
    }

    async delete(primaryKey: any) {
        const modelInstance = await this.getByPk(primaryKey)
        return await modelInstance.destroy()
    }

}