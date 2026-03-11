
const usuario = JSON.parse(sessionStorage.getItem("usuario"));

// Redireciona se não estiver logado ou não for funcionário
if (!usuario || usuario.cargo !== "Funcionário") {
  window.location.href = "../inicial/inicial.html";
}

// ─── Preenche sidebar ────────────────────────────────────────────────────────
document.querySelector(".user-name").textContent = usuario.nome;
document.querySelector(".user-role").textContent = usuario.cargo;

document.querySelectorAll(".chip").forEach(chip => {
  const texto = chip.querySelector("span").textContent;
  if (texto.includes("ID"))    chip.querySelector("strong").textContent = usuario.id;
  if (texto.includes("Turno")) chip.querySelector("strong").textContent = usuario.turno;
});

document.querySelectorAll(".shift-badge").forEach(el => {
  el.innerHTML = `<i class="fa-regular fa-sun"></i> ${usuario.turno}`;
});

// ─── Relógio ─────────────────────────────────────────────────────────────────
function updateClock() {
  const now = new Date();
  const h = String(now.getHours()).padStart(2, "0");
  const m = String(now.getMinutes()).padStart(2, "0");
  const s = String(now.getSeconds()).padStart(2, "0");
  document.getElementById("clock").textContent = `${h}:${m}:${s}`;
  const opts = { weekday: "long", day: "2-digit", month: "long" };
  document.getElementById("date").textContent = now.toLocaleDateString("pt-BR", opts);
}
updateClock();
setInterval(updateClock, 1000);

// ─── Carrega apenas os registros do funcionário logado ───────────────────────
async function carregarRegistros() {
  const tbody = document.querySelector("tbody");

  try {
    const resp = await fetch(`http://localhost:8080/dashboard/${usuario.id}`);
    const texto = await resp.text();

    // O backend retorna List<Ponto>, que o Gson serializa normalmente
    const dados = JSON.parse(texto);

    if (!resp.ok || !Array.isArray(dados) || dados.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="5" style="text-align:center; padding: 24px; color: #8a9baa;">
            Nenhum registro encontrado. Use o botão de chegada para iniciar.
          </td>
        </tr>`;
      return;
    }

    tbody.innerHTML = dados.map(r => {
      const data = r.dataRegistro ? formatarData(r.dataRegistro) : "-";
      return `
        <tr>
          <td class="td-date">${data}</td>
          <td class="td-time entrada">${r.horarioChegada    ?? "-"}</td>
          <td class="td-time">        ${r.horarioSaidaAlmoco ?? "-"}</td>
          <td class="td-time">        ${r.horarioVoltaAlmoco ?? "-"}</td>
          <td class="td-time saida">  ${r.horarioSaida       ?? "-"}</td>
        </tr>`;
    }).join("");

  } catch (err) {
    tbody.innerHTML = `
      <tr>
        <td colspan="5" style="text-align:center; padding: 24px; color: #e05c5c;">
          Erro ao conectar ao servidor.
        </td>
      </tr>`;
    console.error(err);
  }
}

function formatarData(dataStr) {
  // "2026-03-11" → "11/03"
  const [, mes, dia] = dataStr.split("-");
  return `${dia}/${mes}`;
}

// ─── Registrar ponto ─────────────────────────────────────────────────────────
const estados = [
  { texto: "Saída para o Almoço", icone: "fa-utensils",                cor: "#5b8dee", sombra: "rgba(91,141,238,0.4)",  textoCor: "#fff"    },
  { texto: "Volta do Almoço",     icone: "fa-rotate-left",             cor: "#c8a96e", sombra: "rgba(200,169,110,0.4)", textoCor: "#1a1a2e" },
  { texto: "Horário de Saída",    icone: "fa-arrow-right-from-bracket", cor: "#e05555", sombra: "rgba(224,85,85,0.4)",  textoCor: "#fff"    },
];
let estadoAtual = 0;

async function registrarPonto() {
  const btn = document.querySelector(".btn-chegada");
  btn.disabled = true;

  try {
    // ✅ URL corrigida: inclui o ID do usuário logado
    const resp = await fetch(`http://localhost:8080/dashboard/${usuario.id}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });

    const texto = await resp.text();

    if (!resp.ok) {
      alert(texto || "Erro ao registrar ponto.");
      btn.disabled = false;
      return;
    }

    if (estadoAtual < estados.length) {
      const e = estados[estadoAtual];
      btn.innerHTML    = `<i class="fa-solid ${e.icone}"></i> &nbsp;${e.texto}`;
      btn.style.background  = e.cor;
      btn.style.color       = e.textoCor;
      btn.style.boxShadow   = `0 4px 14px ${e.sombra}`;
      btn.disabled = false;
      estadoAtual++;
    } else {
      btn.innerHTML         = `<i class="fa-solid fa-check"></i> &nbsp;Expediente encerrado`;
      btn.style.background  = "#4caf81";
      btn.style.color       = "#fff";
      btn.style.boxShadow   = "0 4px 14px rgba(76,175,129,0.4)";
      btn.disabled = true;
    }

    await carregarRegistros();

  } catch (err) {
    alert("Não foi possível conectar ao servidor.");
    btn.disabled = false;
    console.error(err);
  }
}

// ─── Inicializa ───────────────────────────────────────────────────────────────
carregarRegistros();
