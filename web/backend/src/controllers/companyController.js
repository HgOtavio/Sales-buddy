const companyService = require('../services/companyService');

const { handleError } = require('../utils/errorHandler');
const { validateRequiredFields } = require('../utils/validators');

exports.updateCompany = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['id']);
        if (errorMsg) return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });

        const { id, ...data } = req.body;
        const requesterId = req.user ? req.user.id : null;

        if (Object.keys(data).length === 0) {
            return res.status(400).json({ 
                error: "Nada para atualizar", 
                message: "Envie pelo menos um campo no JSON (além do ID) para ser atualizado." 
            });
        }

        const result = await companyService.updateCompany(id, requesterId, data);
        
        return res.json(result);

    } catch (error) {
        handleError(res, error);
    }
};

exports.getCompanyDetails = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['id']);
        if (errorMsg) return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });

        const { id } = req.body;
        
        const company = await companyService.getCompanyDetails(id);
        return res.json(company);

    } catch (error) {
        handleError(res, error);
    }
};

exports.getAllCompanies = async (req, res) => {
    try {
        const companies = await companyService.getAllCompanies();
        return res.json(companies);
    } catch (error) {
        handleError(res, error);
    }
};