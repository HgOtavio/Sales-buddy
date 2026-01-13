const { Sale, Item } = require('../models');

exports.createSale = async (req, res) => {
    try {
        const { 
            userId, clientName, clientCpf, clientEmail, 
            saleValue, receivedValue, items 
        } = req.body;

        if (!saleValue || !receivedValue) {
            return res.status(400).json({ error: "Values are required" });
        }

        const vSale = parseFloat(saleValue);
        const vReceived = parseFloat(receivedValue);

        if (vReceived < vSale) {
            return res.status(402).json({ 
                error: "Insufficient funds", 
                missing: (vSale - vReceived).toFixed(2) 
            });
        }

        const calculatedChange = vReceived - vSale;

        const newSale = await Sale.create({
            UserId: userId,
            clientName,
            clientCpf,
            clientEmail,
            saleValue: vSale,
            receivedValue: vReceived,
            change: calculatedChange
        });

        if (items && items.length > 0) {
            const itemList = items.map(item => ({
                name: typeof item === 'string' ? item : item.name,
                price: item.price || 0,
                SaleId: newSale.id
            }));
            await Item.bulkCreate(itemList);
        }

        res.status(201).json({ status: "success", saleId: newSale.id, change: calculatedChange });

    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Error creating sale" });
    }
};

exports.getDashboard = async (req, res) => {
    try {
        const { userId } = req.params;
        const sales = await Sale.findAll({
            where: { UserId: userId },
            include: [{ model: Item, as: 'saleItems' }],
            order: [['createdAt', 'DESC']]
        });
        res.json(sales);
    } catch (error) {
        res.status(500).json({ error: "Error fetching sales" });
    }
};