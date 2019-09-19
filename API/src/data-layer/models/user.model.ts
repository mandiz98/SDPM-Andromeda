import {Table, Column, Model, HasMany, DataType, PrimaryKey, CreatedAt, UpdatedAt, DeletedAt} from 'sequelize-typescript';
 
@Table({ tableName: "users", timestamps: false })
export default class User extends Model<User> {

    @PrimaryKey
    @Column(DataType.INTEGER)
    id: number

    @Column
    rfid: String

    @Column
    clockedIn: Boolean

    @Column
    hazmatSuite: Boolean
}