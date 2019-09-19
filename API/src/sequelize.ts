import {Sequelize} from 'sequelize-typescript'

const sequelize = new Sequelize({
    //host: "85.10.205.173",
    database: 'andromeda_db',
    dialect: 'sqlite',
    username: 'root',
    //password: '123123123',
    //port: 3306,
    storage: ":memory:",
    models: [ __dirname + '/data-layer/models/*.model.js' ]
})
sequelize.sync()

console.log( './data-layer/models/*.model.ts');


export default sequelize