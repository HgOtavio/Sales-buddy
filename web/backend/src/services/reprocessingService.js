const { sequelize, Reprocessing, ReprocessingItem, Sale, SaleItem } = require('../models');

exports.createReprocessingEntry = async (data) => {
    const transaction = await sequelize.transaction();
    try {
        const { userId, clientName, clientCpf, clientEmail, saleValue, receivedValue, change, errorReason, items } = data;

        if (!userId) {
            throw { status: 400, message: "ID do lojista é obrigatório." };
        }

        if (!clientName || String(clientName).trim() === "") {
            throw { status: 400, message: "O Nome do Cliente é obrigatório." };
        }

        if (!saleValue || isNaN(saleValue) || Number(saleValue) <= 0) {
            throw { status: 400, message: "O Valor da Venda é obrigatório." };
        }

        if (!items || !Array.isArray(items) || items.length === 0) {
            throw { status: 400, message: "A venda precisa ter pelo menos um item." };
        }

        const cleanCpf = clientCpf ? String(clientCpf).replace(/[^\d]+/g, '') : "";

        const newRepo = await Reprocessing.create({
            userId,
            clientName: clientName.trim(),
            clientCpf: cleanCpf,
            clientEmail: clientEmail || "",
            saleValue,
            receivedValue: receivedValue || 0,
            change: change || 0,
            errorReason: errorReason || "Motivo não informado"
        }, { transaction });

        const itemsData = items.map(item => {
            if ((!item.productName && !item.name) || (!item.totalItemPrice && !item.price)) {
                throw { status: 400, message: "Item inválido encontrado." };
            }

            return {
                reprocessingId: newRepo.id,
                productName: item.productName || item.name,
                quantity: item.quantity || 1,
                unitPrice: item.unitPrice || item.price,
                totalItemPrice: item.totalItemPrice
            };
        });

        await ReprocessingItem.bulkCreate(itemsData, { transaction });

        await transaction.commit();
        return newRepo;

    } catch (error) {
        await transaction.rollback();
        throw error;
    }
};

exports.listPending = async (userId) => {
    return await Reprocessing.findAll({
        where: { userId },
        include: [{ model: ReprocessingItem, as: 'reprocessingItems' }],
        order: [['createdAt', 'DESC']]
    });
};

exports.approveAndMoveToSale = async (reprocessingId) => {
    const transaction = await sequelize.transaction();

    try {
        const pendingData = await Reprocessing.findByPk(reprocessingId, {
            include: [{ model: ReprocessingItem, as: 'reprocessingItems' }],
            transaction
        });

        if (!pendingData) {
            throw { status: 404, message: "Item não encontrado." };
        }

        const newSale = await Sale.create({
            userId: pendingData.userId,
            clientName: pendingData.clientName,
            clientCpf: pendingData.clientCpf,
            clientEmail: pendingData.clientEmail,
            saleValue: pendingData.saleValue,
            receivedValue: pendingData.receivedValue,
            change: pendingData.change
        }, { transaction });

        if (pendingData.reprocessingItems && pendingData.reprocessingItems.length > 0) {
            const itemsToMove = pendingData.reprocessingItems.map(item => ({
                saleId: newSale.id,
                productName: item.productName,
                quantity: item.quantity,
                unitPrice: item.unitPrice,
                totalItemPrice: item.totalItemPrice
            }));
            await SaleItem.bulkCreate(itemsToMove, { transaction });
        }

        await pendingData.destroy({ transaction });

        await transaction.commit();
        return newSale;

    } catch (error) {
        await transaction.rollback();
        throw error;
    }
};