import * as dotenv from 'dotenv';
dotenv.config();

import knex, { Knex } from 'knex';

const options = {
    client: 'mysql2',
    connection: {
        uri: process.env.DATABASE_URL
    }
} as Knex.Config;

export default knex(options);
