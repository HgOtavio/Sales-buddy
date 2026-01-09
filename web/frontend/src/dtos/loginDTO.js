export default class LoginDTO {
  constructor(email, password) {
    if (!email || !password) {
      throw new Error("Usuário e senha são obrigatórios");
    }

    this.email = email;
    this.password = password;
  }
}
