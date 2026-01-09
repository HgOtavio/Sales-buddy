const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.Database('./db.', (err) => {
    if (err) {
        console.error(err.message);
    }
    console.log('SQLite connected');
});

db.run('PRAGMA foreign_keys = ON');

db.serialize(() => {

    // People
    db.run(`
        CREATE TABLE IF NOT EXISTS people (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            cpf TEXT NOT NULL UNIQUE,
            email TEXT NOT NULL UNIQUE
        )
    `);

    // Items
    db.run(`
        CREATE TABLE IF NOT EXISTS items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            price REAL NOT NULL
        )
    `);

    // Sales
    db.run(`
        CREATE TABLE IF NOT EXISTS sales (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            person_id INTEGER NOT NULL,
            sale_date TEXT DEFAULT CURRENT_TIMESTAMP,
            total_value REAL NOT NULL,
            received_value REAL NOT NULL,
            change_value REAL NOT NULL,
            FOREIGN KEY (person_id) REFERENCES people(id)
        )
    `);

    // Sale Items (quantidade aqui)
    db.run(`
        CREATE TABLE IF NOT EXISTS sale_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            sale_id INTEGER NOT NULL,
            item_id INTEGER NOT NULL,
            quantity INTEGER NOT NULL,
            unit_price REAL NOT NULL,
            FOREIGN KEY (sale_id) REFERENCES sales(id),
            FOREIGN KEY (item_id) REFERENCES items(id)
        )
    `);
});

module.exports = db;
