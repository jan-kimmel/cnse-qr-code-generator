import { onAuthChange, getIdToken, login, register, logout } from "./auth.js";

const backendAddress = "http://192.168.178.40:8080/api/qrcodes"

function showLoggedInUI(user) {
    const accountBtns = document.querySelector(".account-btns");

    accountBtns.innerHTML = `
        <button id="history-btn">Verlauf</button>
        <button id="logout-btn">Logout</button>
        `;

    document.getElementById("logout-btn").addEventListener("click", async () => {
        await logout();
    });

    document.getElementById("history-btn").addEventListener("click", async () => {
        await showHistory();
    });
    document.getElementById("history-btn").onclick = showHistory;
    document.getElementById("close-history").onclick = closeDrawer;
    overlay.onclick = closeDrawer;
}

function showLoggedOutUI() {
    const accountBtns = document.querySelector(".account-btns");

    accountBtns.innerHTML = `
        <button id="register-btn">Registrieren</button>
        <button id="login-btn">Einloggen</button>
    `;

    connectAuthButtons();
}

onAuthChange(user => {
    if (user) {
        showLoggedInUI(user);
        console.log("Eingeloggt:", user.email);
    } else {
        showLoggedOutUI();
        console.log("Nicht eingeloggt");
    }
});

function connectAuthButtons() {
    const loginBtn = document.getElementById("login-btn");
    const registerBtn = document.getElementById("register-btn");

    if (loginBtn) loginBtn.addEventListener("click", () => openModal("login"));
    if (registerBtn) registerBtn.addEventListener("click", () => openModal("register"));
}
connectAuthButtons();

const qrForm = document.getElementById("qr-form");
const qrText = document.getElementById("qr-text");
const qrImage = document.getElementById("qr-image");
const qrPngDownload = document.getElementById("qr-png-download-btn");
let currentQrId = null;

qrText.focus();

qrForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const token = await getIdToken();

    const headers = {"Content-Type": "application/json"};
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const response = await fetch(backendAddress, {
        method: "POST",
        headers,
        body: JSON.stringify({text: qrText.value})
    });

    if (!response.ok) {
        alert("Fehler beim Erstellen des QR-Codes");
        return;
    }

    const data = await response.json();
    currentQrId = data.id;
    qrImage.src = data.imageURL;
    qrImage.hidden = false;
    qrPngDownload.classList.remove("button-disabled");
})

qrPngDownload.addEventListener("click", (e) => {
    if (!currentQrId) {
        e.preventDefault();
        return;
    }

    const downloadUrl = `${backendAddress}/${currentQrId}/download.png`;

    const link = document.createElement("a");
    link.href = downloadUrl;
    link.download = `qrcode-${currentQrId}.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
});



const authModal = document.getElementById("auth-modal");
const modalTitle = document.getElementById("modal-title");
const modalEmail = document.getElementById("modal-email");
const modalPassword = document.getElementById("modal-password");
const modalSubmit = document.getElementById("modal-submit");
const closeModal = document.getElementById("close-modal");

let modalMode = "login";

function openModal(mode) {
    modalMode = mode;
    modalTitle.textContent = mode === "login" ? "Einloggen" : "Registrieren";
    modalSubmit.textContent = mode === "login" ? "Einloggen" : "Registrieren";
    modalEmail.value = "";
    modalPassword.value = "";
    authModal.classList.remove("hidden");
}

closeModal.addEventListener("click", () => authModal.classList.add("hidden"));
authModal.addEventListener("click", e => { if(e.target === authModal) authModal.classList.add("hidden"); });

modalSubmit.addEventListener("click", async () => {
    const email = modalEmail.value.trim();
    const password = modalPassword.value.trim();

    if (!email || !password) {
        alert("Bitte Email und Passwort eingeben.");
        return;
    }

    try {
        if (modalMode === "login") {
            await login(email, password);
            alert("Erfolgreich eingeloggt!");
        } else {
            await register(email, password);
            alert("Registrierung erfolgreich!");
        }
        authModal.classList.add("hidden");
    } catch (err) {
        alert(`${modalMode === "login" ? "Login" : "Registrierung"} fehlgeschlagen: ${err.message}`);
    }
});



const drawer = document.getElementById("history-drawer");
const overlay = document.getElementById("drawer-overlay");

function openDrawer() {
  drawer.classList.add("open");
  drawer.classList.remove("hidden");
  overlay.classList.remove("hidden");
}

function closeDrawer() {
  drawer.classList.remove("open");
  overlay.classList.add("hidden");
  setTimeout(() => drawer.classList.add("hidden"), 300);
}


async function showHistory() {
  openDrawer();

  const token = await getIdToken();

  const res = await fetch(`${backendAddress}/history`, {
    headers: {
      "Authorization": "Bearer " + token
    }
  });

  if (!res.ok) {
    alert("Bitte einloggen");
    return;
  }

  const history = await res.json();
  const list = document.getElementById("history-list");
  list.innerHTML = "";

  history.forEach(text => {
    const li = document.createElement("li");
    li.textContent = text;

    li.onclick = () => {
      selectHistoryItem(text);
    };

    list.appendChild(li);
  });
}

function selectHistoryItem(text) {
  closeDrawer();

  const input = document.getElementById("qr-text");
  input.value = text;

  qrForm.requestSubmit();
}