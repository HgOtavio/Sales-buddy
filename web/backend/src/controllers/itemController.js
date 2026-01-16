const itemService = require('../services/itemService');

exports.listItems = async (req, res) => {
    try {
        const { saleId } = req.params;
        if (!saleId) return res.status(400).json({ error: "Sale ID required" });

        const items = await itemService.getItemsBySaleId(saleId);
        res.json(items);
    } catch (error) {
        res.status(error.status || 500).json({ error: error.message });
    }
};

exports.addItem = async (req, res) => {
    try {
        const { saleId } = req.params;
        const { name, price } = req.body;

        if (!name) return res.status(400).json({ error: "Item name is required" });

        const newItem = await itemService.addItemToSale(saleId, { name, price });
        res.status(201).json(newItem);
    } catch (error) {
        res.status(error.status || 500).json({ error: error.message });
    }
};