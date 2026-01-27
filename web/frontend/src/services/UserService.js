// src/services/UserService.js
import api from './api';
import { ENDPOINTS } from './endpoints';

export const UserService = {
    register: (payload) => {
        return api.post(ENDPOINTS.AUTH.REGISTER, payload);
    },

    update: (payload) => {
        return api.put(ENDPOINTS.AUTH.USERS, payload);
    },

    resetPassword: (email) => {
        return api.post(ENDPOINTS.AUTH.FORGOT_PASSWORD, { email });
    },
    getAll: () => {
        return api.get(ENDPOINTS.AUTH.USERS);
    },

    delete: (userId) => {
        // Axios delete com body precisa da propriedade 'data'
        return api.delete(ENDPOINTS.AUTH.USERS, { 
            data: { id: userId } 
        });
    }
};