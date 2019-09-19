import { Table, Column, Model, DataType, PrimaryKey, AutoIncrement } from 'sequelize-typescript';
 
@Table({ tableName: "users", timestamps: false })
export default class User extends Model<User> {

    @PrimaryKey
    @AutoIncrement
    @Column(DataType.INTEGER)
    id: number

    @Column({ allowNull: false, unique: true })
    rfid: String

    @Column({ defaultValue: false })
    clockedIn: Boolean

    @Column({ defaultValue: false })
    hazmatSuite: Boolean
}