const usuario = JSON.parse(sessionStorage.getItem("usuario"));

if (!usuario || usuario.cargo !== "Gerente") {
  window.location.href = "./inicial.html";
}

document.querySelector(".user-name").textContent = usuario.nome;
document.querySelector(".user-role").textContent = usuario.cargo;

document.querySelectorAll(".chip").forEach(chip => {
  const texto = chip.querySelector("span").textContent;
  if (texto.includes("ID")) chip.querySelector("strong").textContent = usuario.id;
  if (texto.includes("Turno")) chip.querySelector("strong").textContent = usuario.turno;
});

document.querySelectorAll(".shift-badge").forEach(el => {
  el.innerHTML = `<i class="fa-regular fa-sun"></i> ${usuario.turno}`;
});

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

document.body.insertAdjacentHTML("beforeend", `
  <div id="modalOverlay" style="
    display:none; position:fixed; inset:0;
    background:rgba(0,0,0,0.4); z-index:1000;
    align-items:center; justify-content:center;">
    <div style="
      background:#fff; border-radius:16px; padding:32px;
      width:360px; box-shadow:0 8px 32px rgba(0,0,0,0.15);
      font-family:'DM Sans',sans-serif;">
      <h3 id="modalTitulo" style="margin-bottom:20px; color:#1a2e35; font-size:1rem;"></h3>
      <div style="display:flex; flex-direction:column; gap:12px;">
        <label style="font-size:0.75rem; color:#5a7080; text-transform:uppercase; letter-spacing:0.04em;">Chegada</label>
        <input id="editChegada" type="time" step="1" style="padding:8px 12px; border:1.5px solid #e2eaee; border-radius:8px; font-size:0.9rem;">
        <label style="font-size:0.75rem; color:#5a7080; text-transform:uppercase; letter-spacing:0.04em;">Saída almoço</label>
        <input id="editSaidaAlmoco" type="time" step="1" style="padding:8px 12px; border:1.5px solid #e2eaee; border-radius:8px; font-size:0.9rem;">
        <label style="font-size:0.75rem; color:#5a7080; text-transform:uppercase; letter-spacing:0.04em;">Volta almoço</label>
        <input id="editVoltaAlmoco" type="time" step="1" style="padding:8px 12px; border:1.5px solid #e2eaee; border-radius:8px; font-size:0.9rem;">
        <label style="font-size:0.75rem; color:#5a7080; text-transform:uppercase; letter-spacing:0.04em;">Saída</label>
        <input id="editSaida" type="time" step="1" style="padding:8px 12px; border:1.5px solid #e2eaee; border-radius:8px; font-size:0.9rem;">
      </div>
      <div style="display:flex; gap:10px; margin-top:24px;">
        <button onclick="fecharModal()" style="
          flex:1; padding:10px; border:none; border-radius:8px;
          background:#f0f4f8; color:#5a7080; cursor:pointer; font-size:0.9rem;">
          Cancelar
        </button>
        <button onclick="salvarEdicao()" style="
          flex:2; padding:10px; border:none; border-radius:8px;
          background:#4bbfc6; color:#fff; cursor:pointer; font-size:0.9rem; font-weight:600;">
          Salvar
        </button>
      </div>
    </div>
  </div>
`);

// Estado do modal
let _editUsuarioId = null;
let _editData = null;

function abrirEdicao(usuarioId, data, nome, chegada, saidaAlmoco, voltaAlmoco, saida) {
  _editUsuarioId = usuarioId;
  _editData = data;

  document.getElementById("modalTitulo").textContent = `Editando registro de ${nome} — ${data}`;

  // Preenche os campos com os valores atuais (remove segundos se vier HH:MM:SS)
  document.getElementById("editChegada").value = chegada ? chegada.substring(0, 5) : "";
  document.getElementById("editSaidaAlmoco").value = saidaAlmoco ? saidaAlmoco.substring(0, 5) : "";
  document.getElementById("editVoltaAlmoco").value = voltaAlmoco ? voltaAlmoco.substring(0, 5) : "";
  document.getElementById("editSaida").value = saida ? saida.substring(0, 5) : "";

  const overlay = document.getElementById("modalOverlay");
  overlay.style.display = "flex";
}

function fecharModal() {
  document.getElementById("modalOverlay").style.display = "none";
}

async function salvarEdicao() {
  const toSegundos = v => { if (!v) return null; return v.split(":").length === 2 ? v + ":00" : v; };

  const body = {
    dataRegistro: _editData,
    horarioChegada: toSegundos(document.getElementById("editChegada").value),
    horarioSaidaAlmoco: toSegundos(document.getElementById("editSaidaAlmoco").value),
    horarioVoltaAlmoco: toSegundos(document.getElementById("editVoltaAlmoco").value),
    horarioSaida: toSegundos(document.getElementById("editSaida").value),
  };

  Object.keys(body).forEach(k => { if (body[k] === null) delete body[k]; });

  try {
    const resp = await fetch(`http://localhost:8080/dashboard-gerente/${_editUsuarioId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    const msg = await resp.text();
    fecharModal();
    alert(msg);
    await carregarRegistros();
  } catch (err) {
    alert("Erro ao salvar.");
    console.error(err);
  }
}

document.getElementById("modalOverlay").addEventListener("click", e => {
  if (e.target === document.getElementById("modalOverlay")) fecharModal();
});

async function carregarRegistros() {
  const tbody = document.querySelector("tbody");

  try {
    const resp = await fetch(`http://localhost:8080/dashboard-gerente/${usuario.id}`);
    const texto = await resp.text();

    if (!resp.ok) {
      tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;padding:24px;color:#e05c5c;">${texto}</td></tr>`;
      return;
    }

    const dados = JSON.parse(texto);
    if (!Array.isArray(dados) || dados.length === 0) {
      tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;padding:24px;color:#8a9baa;">Nenhum registro encontrado.</td></tr>`;
      return;
    }

const statValue = document.querySelector(".stat-value");
if (statValue) {
  fetch("http://localhost:8080/usuarios")
    .then(r => r.json())
    .then(usuarios => { statValue.textContent = usuarios.length; })
    .catch(() => { statValue.textContent = ids.length; }); // fallback
}
    tbody.innerHTML = dados.map(r => {
      const data = r.dataRegistro ? formatarData(r.dataRegistro) : "-";
      const ch = r.horarioChegada ?? "";
      const sa = r.horarioSaidaAlmoco ?? "";
      const va = r.horarioVoltaAlmoco ?? "";
      const s = r.horarioSaida ?? "";
      const nome = (r.nomeUsuario ?? "").replace(/'/g, "\\'");

      return `
        <tr>
          <td class="td-date">${data}</td>
          <td class="td-id">#${r.usuariosId}</td>
          <td class="td-name">${r.nomeUsuario ?? "-"}</td>
          <td class="td-time entrada">${ch || "-"}</td>
          <td class="td-time">${sa || "-"}</td>
          <td class="td-time">${va || "-"}</td>
          <td class="td-time saida">${s || "-"}</td>
          <td>
            <button class="btn-edit"
              onclick="abrirEdicao(${r.usuariosId},'${r.dataRegistro}','${nome}','${ch}','${sa}','${va}','${s}')">
              <i class="fa-solid fa-pen"></i>
            </button>
          </td>
        </tr>`;
    }).join("");

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;padding:24px;color:#e05c5c;">Erro ao conectar ao servidor.</td></tr>`;
    console.error(err);
  }
}

function formatarData(dataStr) {
  const [, mes, dia] = dataStr.split("-");
  return `${dia}/${mes}`;
}

// ─── Registrar ponto ──────────────────────────────────────────────────────────
const estados = [
  { texto: "Saída para o Almoço", icone: "fa-utensils", cor: "#5b8dee", sombra: "rgba(91,141,238,0.4)", textoCor: "#fff" },
  { texto: "Volta do Almoço", icone: "fa-rotate-left", cor: "#c8a96e", sombra: "rgba(200,169,110,0.4)", textoCor: "#1a1a2e" },
  { texto: "Horário de Saída", icone: "fa-arrow-right-from-bracket", cor: "#e05555", sombra: "rgba(224,85,85,0.4)", textoCor: "#fff" },
];
let estadoAtual = 0;

async function registrarChegada() {
  const btn = document.querySelector(".btn-chegada");
  btn.disabled = true;

  try {
    const resp = await fetch(`http://localhost:8080/dashboard-gerente/${usuario.id}`, {
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
      btn.innerHTML = `<i class="fa-solid ${e.icone}"></i> &nbsp;${e.texto}`;
      btn.style.background = e.cor;
      btn.style.color = e.textoCor;
      btn.style.boxShadow = `0 4px 14px ${e.sombra}`;
      btn.disabled = false;
      estadoAtual++;
    } else {
      btn.innerHTML = `<i class="fa-solid fa-check"></i> &nbsp;Expediente encerrado`;
      btn.style.background = "#4caf81";
      btn.style.color = "#fff";
      btn.style.boxShadow = "0 4px 14px rgba(76,175,129,0.4)";
      btn.disabled = true;
    }

    await carregarRegistros();
  } catch (err) {
    alert("Não foi possível conectar ao servidor.");
    btn.disabled = false;
    console.error(err);
  }
}
carregarRegistros();