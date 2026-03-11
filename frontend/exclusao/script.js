document.getElementById("cadastrarBotao").addEventListener("click", async () => {
  const nome = document.getElementById("nome").value.trim();
  const id   = document.getElementById("id").value.trim();
  const mensagemErro = document.getElementById("mensagemErro");
  mensagemErro.textContent = "";

  if (!nome || !id) {
    mensagemErro.textContent = "Preencha o nome e o ID do colaborador.";
    return;
  }

  const confirmar = confirm(`Tem certeza que deseja excluir "${nome}" (ID: ${id})? Esta ação não pode ser desfeita.`);
  if (!confirmar) return;

  try {
    const resposta = await fetch(`http://localhost:8080/dashboard-gerente/${id}`, {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
    });

    const texto = await resposta.text();

    if (resposta.ok) {
      alert(`Colaborador "${nome}" excluído com sucesso!`);
      window.location.href = "../adm-page/page.html";
    } else {
      mensagemErro.textContent = texto || "Erro ao excluir colaborador.";
    }
  } catch (erro) {
    mensagemErro.textContent = "Não foi possível conectar ao servidor.";
    console.error(erro);
  }
});