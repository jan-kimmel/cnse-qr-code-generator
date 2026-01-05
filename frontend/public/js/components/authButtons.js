export class AuthButtons {
    constructor(containerElement, onLogin, onRegister, onLogout, onHistory) {
        this.container = containerElement;
        this.onLogin = onLogin;
        this.onRegister = onRegister;
        this.onLogout = onLogout;
        this.onHistory = onHistory;
    }

    showLoggedIn(user) {
        this.container.innerHTML = `
            <button id="history-btn">Verlauf</button>
            <button id="logout-btn">Logout</button>
        `;

        document.getElementById("logout-btn").addEventListener("click", this.onLogout);
        document.getElementById("history-btn").addEventListener("click", this.onHistory);
    }

    showLoggedOut() {
        this.container.innerHTML = `
            <button id="register-btn">Registrieren</button>
            <button id="login-btn">Einloggen</button>
        `;

        const loginBtn = document.getElementById("login-btn");
        const registerBtn = document.getElementById("register-btn");

        if (loginBtn) loginBtn.addEventListener("click", () => this.onLogin());
        if (registerBtn) registerBtn.addEventListener("click", () => this.onRegister());
    }
}