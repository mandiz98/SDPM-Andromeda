import { Table, Column, Model, DataType, PrimaryKey, AutoIncrement, ForeignKey, AfterCreate } from 'sequelize-typescript';
import { EventKey } from './eventKey.enum';
import User from './user.model';
 
@Table({ tableName: "events", timestamps: false })
export default class Event extends Model<Event> {

    @PrimaryKey
    @AutoIncrement
    @Column(DataType.INTEGER)
    id: number

    @Column(DataType.INTEGER)
    @ForeignKey(() => User)
    userId: number

    @Column({ allowNull: false })
    @Column(DataType.INTEGER)
    eventKey: EventKey

    @Column({ allowNull: false, defaultValue: new Date() })
    dateCreated: Date

    @AfterCreate
    static updateUser(instance: Event) {
        let eventKey = instance.eventKey
        // Make sure the event has a user
        if(instance.userId) {
            // Make sure the event is either clock in or out
            if(eventKey == EventKey.CLOCKED_IN || eventKey == EventKey.CLOCKED_OUT) {
                // Update the user's clocked in/out status
                User.update(
                    {
                        clockedIn: eventKey == EventKey.CLOCKED_IN
                    },
                    {
                        where: {
                            id: instance.userId
                        }
                    }
                )
            }
        }
    }
}