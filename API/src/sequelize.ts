import {Sequelize} from 'sequelize-typescript'

const sequelize = new Sequelize({
    //host: "85.10.205.173",
    database: 'andromeda_db',
    dialect: 'sqlite',
    username: 'root',
    //password: '123123123',
    //port: 3306,
    storage: "database.sqlite",
    models: [ __dirname + '/data-layer/models/*.model.js' ]
})
sequelize.sync({ force: false })

export default sequelize