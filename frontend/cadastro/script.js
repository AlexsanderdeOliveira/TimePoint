document.getElementById("cadastrarBotao").addEventListener("click", async () => {
  const inputs = document.querySelectorAll("input");
  const selects = document.querySelectorAll("select");

  const nome  = inputs[0].value.trim();
  const email = inputs[1].value.trim();
  const senha = inputs[2].value.trim();
  const cargo = selects[0].value;
  const turno = selects[1].value;

  const mensagemErro = document.getElementById("mensagemErro");
  mensagemErro.textContent = "";

  if (!nome || !email || !senha) {
    mensagemErro.textContent = "Preencha todos os campos.";
    return;
  }

  try {
    const resposta = await fetch("http://localhost:8080/usuarios", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nome, email, senha, cargo, turno }),
    });

    const texto = await resposta.text();

    if (resposta.ok) {
      alert(`Colaborador "${nome}" cadastrado com sucesso!`);
      window.location.href = "../adm-page/page.html";
    } else {
      mensagemErro.textContent = texto || "Erro ao cadastrar colaborador.";
    }
  } catch (erro) {
    mensagemErro.textContent = "Não foi possível conectar ao servidor.";
    console.error(erro);
  }
});