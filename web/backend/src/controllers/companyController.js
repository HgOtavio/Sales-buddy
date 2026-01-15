const Company = require('../models/Company');
const User = require('../models/User');
const { isValidCNPJ } = require('../utils/userHelpers');

exports.updateCompany = async (req, res) => {
    try {
        const { id } = req.params; // ID da empresa que queremos editar
        const { name, taxId } = req.body;
        const requesterId = req.user ? req.user.id : null; // Quem está pedindo


        const company = await Company.findByPk(id);

        if (!company) {
            return res.status(404).json({ error: "Empresa não encontrada." });
        }

     
        
        if (!requesterId) {
            return res.status(401).json({ error: "Usuário não autenticado." });
        }

        const requester = await User.findByPk(requesterId);
        
        if (!requester || String(requester.companyId) !== String(company.id)) {
            console.log(`[BLOQUEIO] Usuário pertence à empresa ${requester.companyId}, mas tentou mexer na ${company.id}`);
            return res.status(403).json({ error: "Você não tem permissão para editar esta empresa." });
        }

        const ownerUser = await User.findOne({
            where: { companyId: company.id },
            order: [['id', 'ASC']] 
        });


        if (ownerUser && String(ownerUser.id) !== String(requester.id)) {
            console.log(`[BLOQUEIO] O usuário ${requester.id} tentou editar, mas o dono é ${ownerUser.id}`);
            return res.status(403).json({ 
                error: `Acesso negado. Apenas o criador da empresa (${ownerUser.name}) pode alterar estes dados.` 
            });
        }
        
        if (name) {
            company.name = name;
        }

        if (taxId) {
            const cleanTaxId = String(taxId).replace(/[^\d]+/g, '');

            if (!isValidCNPJ(cleanTaxId)) {
                return res.status(400).json({ error: "CNPJ inválido." });
            }

            const duplicateCompany = await Company.findOne({ where: { taxId: cleanTaxId } });
            if (duplicateCompany && duplicateCompany.id !== company.id) {
                return res.status(409).json({ error: "Este CNPJ já está cadastrado em outra empresa." });
            }

            company.taxId = cleanTaxId;
        }

        await company.save();

        return res.json({ 
            message: "Dados da empresa atualizados com sucesso!",
            company: {
                id: company.id,
                name: company.name,
                taxId: company.taxId
            }
        });

    } catch (error) {
        console.error(error);
        return res.status(500).json({ error: "Erro interno ao atualizar empresa." });
    }
};

exports.getCompanyDetails = async (req, res) => {
    try {
        const { id } = req.params;
        const company = await Company.findByPk(id, {
            include: [{ model: User, attributes: ['id', 'name', 'user', 'email'] }]
        });
        if (!company) return res.status(404).json({ error: "Empresa não encontrada." });
        return res.json(company);
    } catch (error) {
        return res.status(500).json({ error: "Erro ao buscar detalhes." });
    }
};

exports.getAllCompanies = async (req, res) => {
    try {
        const companies = await Company.findAll({
            include: [{ model: User, attributes: ['id'] }]
        });
        const formatted = companies.map(c => ({
            id: c.id,
            name: c.name,
            taxId: c.taxId,
            userCount: c.Users.length
        }));
        return res.json(formatted);
    } catch (error) {
        return res.status(500).json({ error: "Erro ao listar empresas." });
    }
};