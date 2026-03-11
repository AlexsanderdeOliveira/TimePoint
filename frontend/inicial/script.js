function getSaudacao() {
  const hora = new Date().getHours();
  if (hora >= 5 && hora < 12) return "Bom dia!";
  if (hora >= 12 && hora < 18) return "Boa tarde!";
  return "Boa noite!";
}

document.addEventListener("DOMContentLoaded", () => {
  const saudacao = document.querySelector("h1");
  if (saudacao) {
    saudacao.textContent = `${getSaudacao()} Bem-vindo de volta.`;
  }
});

async function fazerLogin() {
  const email = document.getElementById("email").value.trim();
  const senha = document.getElementById("senha").value.trim();
  const mensagemErro = document.getElementById("mensagemErro");

  mensagemErro.textContent = "";

  if (!email || !senha) {
    mensagemErro.textContent = "Preencha o e-mail e a senha.";
    return;
  }

  try {
    const resposta = await fetch("http://localhost:8080/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, senha }),
    });

    const texto = await resposta.text();

    if (resposta.ok) {
      const dados = JSON.parse(texto);
      sessionStorage.setItem("usuario", JSON.stringify(dados));

      if (dados.cargo === "Gerente") {
        window.location.href = "../adm-page/page.html";
      } else {
        window.location.href = "../user-page/page.html";
      }
    } else {
      mensagemErro.textContent = texto || "Email ou senha incorretos.";
    }
  } catch (erro) {
    mensagemErro.textContent = "Não foi possível conectar ao servidor.";
    console.error(erro);
  }
}

function forgotPassword() {
  alert("Entre em contato com o administrador do sistema para redefinir sua senha.");
}