import { login, register } from "../auth.js";

export class AuthModal {
    constructor(modalElement) {
        this.modal = modalElement;
        this.title = modalElement.querySelector("#modal-title");
        this.emailInput = modalElement.querySelector("#modal-email");
        this.passwordInput = modalElement.querySelector("#modal-password");
        this.form = modalElement.querySelector("#auth-form");
        this.submitButton = modalElement.querySelector("#modal-submit");
        this.closeButton = modalElement.querySelector("#close-modal");
        this.mode = "login";

        this.init();
    }

    init() {
        this.closeButton.addEventListener("click", () => this.close());
        this.modal.addEventListener("click", (e) => {
            if (e.target === this.modal) this.close();
        });
        this.form.addEventListener("submit", (e) => {
            e.preventDefault();
            this.handleSubmit();
        });
    }

    open(mode) {
        this.mode = mode;
        this.title.textContent = mode === "login" ? "Einloggen" : "Registrieren";
        this.submitButton.textContent = mode === "login" ? "Einloggen" : "Registrieren";
        this.emailInput.value = "";
        this.passwordInput.value = "";
        this.modal.classList.remove("hidden");
        this.emailInput.focus();
    }

    close() {
        this.modal.classList.add("hidden");
    }

    async handleSubmit() {
        const email = this.emailInput.value.trim();
        const password = this.passwordInput.value.trim();

        if (!email || !password) {
            alert("Bitte Email und Passwort eingeben.");
            return;
        }

        try {
            if (this.mode === "login") {
                await login(email, password);
                alert("Erfolgreich eingeloggt!");
            } else {
                await register(email, password);
                alert("Registrierung erfolgreich!");
            }
            this.close();
        } catch (error) {
            console.error("Auth error:", error);
            alert(`${this.mode === "login" ? "Login" : "Registrierung"} fehlgeschlagen: ${error.message}`);
        }
    }
}