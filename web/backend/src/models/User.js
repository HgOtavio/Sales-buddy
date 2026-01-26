const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/dbConfig');
const Company = require('./Company'); 

const User = sequelize.define('User', {
    name: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    user: { 
        type: DataTypes.STRING,
        allowNull: false,
        unique: true 
    },
    email: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: true,
    },
    password: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    companyId: {
        type: DataTypes.INTEGER,
        references: {
            model: Company,
            key: 'id'
        },
        allowNull: false
    },
    resetToken: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    resetTokenExpires: {
        type: DataTypes.BIGINT, 
        allowNull: true,
    }
}, {
    timestamps: true,
});

User.belongsTo(Company, { foreignKey: 'companyId' });
Company.hasMany(User, { foreignKey: 'companyId' });

module.exports = User;