import { Table, Column, Model, DataType, PrimaryKey, AutoIncrement, ForeignKey, AfterCreate } from 'sequelize-typescript';
import { EventKey } from './eventKey.enum';
import User from './user.model';
 
@Table({ tableName: "events", timestamps: false })
export default class Event extends Model<Event> {

    @PrimaryKey
    @AutoIncrement
    @Column(DataType.INTEGER)
    id: number

    @Column(DataType.STRING)
    @ForeignKey(() => User)
    userId: number

    @Column({ allowNull: false })
    @Column(DataType.INTEGER)
    eventKey: EventKey

    @Column({ allowNull: false, defaultValue: new Date() })
    dateCreated: Date

    @Column({ allowNull: true })
    data: string

    @AfterCreate
    static async updateUser(instance: Event) {
        console.log(instance);
        
        let eventKey = instance.eventKey
        // Make sure the event has a user
        if(instance.data) {
            // Make sure the event is either clock in or out
            if(eventKey == EventKey.CLOCKED_IN_OUT) {
                console.log("hejsan");
                
                // Get user based on rfid
                var user = await User.findOne({
                    where: {
                        rfid: instance.data // data = rfid
                    }
                })
                
                if(!user)
                    return
                
                // Invert the user's clockedIn value
                user.clockedIn = !user.clockedIn
                return await user.save()
            }
        }
    }
}