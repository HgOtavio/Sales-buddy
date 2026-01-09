export function useFloatingActions(setActive) {
  
  function handleGoToAdd() {
    setActive("cadastro");
  }

  function handleSave() {
    alert("Usuário salvo com sucesso!"); 
    setActive("usuarios"); 
  }

  function handleResetPassword() {
    const confirm = window.confirm("Deseja enviar o link de redefinição de senha para este usuário?");
    if (confirm) {
      alert("Link de redefinição enviado com sucesso!");
    }
  }

  return {
    handleGoToAdd,
    handleSave,
    handleResetPassword
  };
}