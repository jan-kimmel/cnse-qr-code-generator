import { onAuthChange, getIdToken, login, register, logout } from "./auth.js";

function showLoggedInUI(user) {
    const accountBtns = document.querySelector(".account-btns");
    const historyBtn = document.getElementById("history-btn");

    accountBtns.innerHTML = `<button id="logout-btn">Logout</button>`;

    historyBtn.classList.remove("button-disabled");

    document.getElementById("logout-btn").addEventListener("click", async () => {
        await logout();
    });
}

function showLoggedOutUI() {
    const accountBtns = document.querySelector(".account-btns");
    const historyBtn = document.getElementById("history-btn");

    accountBtns.innerHTML = `
        <button id="register-btn">Registrieren</button>
        <button id="login-btn">Einloggen</button>
    `;

    historyBtn.classList.add("button-disabled");

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

connectAuthButtons();

const qrForm = document.getElementById("qr-form");
const qrText = document.getElementById("qr-text");
const qrImage = document.getElementById("qr-image");
const qrPngDownload = document.getElementById("qr-png-download-btn")
let currentQrId = null;

qrText.focus();

qrForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const response = await fetch("http://localhost:8080/api/qrcodes", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
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

    const downloadUrl = `http://localhost:8080/api/qrcodes/${currentQrId}/download.png`;

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

function connectAuthButtons() {
    const loginBtn = document.getElementById("login-btn");
    const registerBtn = document.getElementById("register-btn");

    if (loginBtn) loginBtn.addEventListener("click", () => openModal("login"));
    if (registerBtn) registerBtn.addEventListener("click", () => openModal("register"));
}

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