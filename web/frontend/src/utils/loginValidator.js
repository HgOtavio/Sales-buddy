
export function validateLoginInputs(user, password) {
  if (!user || user.trim() === "") {
    return "Por favor, preencha o campo de USUÁRIO.";
  }

  if (!password || password.trim() === "") {
    return "Por favor, preencha o campo de SENHA.";
  }

  return null;
}