// src/utils/browserUtils.js

export const forceDownload = (blobData, filename) => {
    // Cria o objeto Blob invisível
    const url = window.URL.createObjectURL(new Blob([blobData]));
    
    // Cria o link temporário no HTML
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    
    // Clica e remove
    link.click();
    link.parentNode.removeChild(link);
    window.URL.revokeObjectURL(url);
};

export const parseBlobError = async (errorResponse) => {
    // Ajuda a ler o erro JSON que vem escondido dentro de um Blob
    if (errorResponse && errorResponse.data instanceof Blob) {
        const text = await errorResponse.data.text();
        try {
            return JSON.parse(text);
        } catch {
            return null;
        }
    }
    return null;
};