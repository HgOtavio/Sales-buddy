const { Company, User } = require('../models');
const { isValidCNPJ } = require('../utils/userHelpers');

// Helper interno para lançar erros com status
const throwError = (status, message) => {
    const error = new Error(message);
    error.status = status;
    throw error;
};

module.exports = {

    async updateCompany(companyId, requesterId, data) {
        const { name, taxId } = data;

        // 1. Busca a empresa alvo
        const company = await Company.findByPk(companyId);
        if (!company) throwError(404, "Empresa não encontrada.");

        // 2. Busca quem está pedindo a alteração
        if (!requesterId) throwError(401, "Usuário não autenticado.");
        const requester = await User.findByPk(requesterId);

        // 3. SEGURANÇA: Verifica se o usuário pertence à empresa que quer editar
        if (!requester || String(requester.companyId) !== String(company.id)) {
            console.log(`[BLOQUEIO] User ${requester?.id} da empresa ${requester?.companyId} tentou mexer na empresa ${company.id}`);
            throwError(403, "Você não tem permissão para editar esta empresa.");
        }

        // 4. SEGURANÇA: Verifica se é o 'Dono' (primeiro usuário criado na empresa)
        const ownerUser = await User.findOne({
            where: { companyId: company.id },
            order: [['id', 'ASC']]
        });

        if (ownerUser && String(ownerUser.id) !== String(requester.id)) {
            console.log(`[BLOQUEIO] User ${requester.id} tentou editar, mas o dono é ${ownerUser.id}`);
            throwError(403, `Acesso negado. Apenas o criador da empresa (${ownerUser.name}) pode alterar a Razão Social ou CNPJ.`);
        }

        // 5. Atualização de Nome (Com verificação de duplicidade)
        if (name && name !== company.name) {
            const nameExists = await Company.findOne({ where: { name } });
            
            if (nameExists && nameExists.id !== company.id) {
                throwError(409, `O nome "${name}" já está sendo usado por outra empresa.`);
            }
            
            company.name = name;
        }

        // 6. Atualização de CNPJ (Com validação e duplicidade)
        if (taxId) {
            const cleanTaxId = String(taxId).replace(/[^\d]+/g, '');

            if (!isValidCNPJ(cleanTaxId)) {
                throwError(400, "O CNPJ informado é inválido.");
            }

            // Verifica duplicidade de CNPJ
            if (cleanTaxId !== company.taxId) {
                const duplicateCompany = await Company.findOne({ where: { taxId: cleanTaxId } });
                if (duplicateCompany && duplicateCompany.id !== company.id) {
                    throwError(409, "Este CNPJ já está cadastrado em outra empresa.");
                }
                company.taxId = cleanTaxId;
            }
        }

        await company.save();

        return {
            message: "Dados da empresa atualizados com sucesso!",
            company: {
                id: company.id,
                name: company.name,
                taxId: company.taxId
            }
        };
    },

    async getCompanyDetails(id) {
        const company = await Company.findByPk(id, {
            include: [{ 
                model: User, 
                attributes: ['id', 'name', 'user', 'email'] // Retorna apenas dados seguros dos usuários
            }]
        });
        
        if (!company) throwError(404, "Empresa não encontrada.");
        
        return company;
    },

    async getAllCompanies() {
        const companies = await Company.findAll({
            include: [{ model: User, attributes: ['id'] }]
        });

        // Formatação dos dados para o frontend (Dashboard Admin)
        return companies.map(c => ({
            id: c.id,
            name: c.name,
            taxId: c.taxId,
            userCount: c.Users ? c.Users.length : 0
        }));
    }
};