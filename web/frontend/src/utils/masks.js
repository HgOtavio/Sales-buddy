export const maskCNPJ = (value) => {
  if (!value) return "";

  // 1. Remove tudo o que não for número
  let v = value.replace(/\D/g, "");

  // 2. Limita a 14 dígitos (tamanho máximo do CNPJ apenas números)
  if (v.length > 14) v = v.slice(0, 14);

  // 3. Aplica a máscara passo a passo:
  
  // Coloca ponto entre o 2º e o 3º dígitos (ex: 123 -> 12.3)
  v = v.replace(/^(\d{2})(\d)/, "$1.$2");

  // Coloca ponto entre o 5º e o 6º dígitos (ex: 12.3456 -> 12.345.6)
  v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");

  // Coloca uma barra entre o 8º e o 9º dígitos (ex: 12.345.6789 -> 12.345.678/9)
  v = v.replace(/\.(\d{3})(\d)/, ".$1/$2");

  // Coloca um hífen depois do bloco de 4 dígitos (ex: .../000199 -> .../0001-99)
  v = v.replace(/(\d{4})(\d)/, "$1-$2");

  return v;
};