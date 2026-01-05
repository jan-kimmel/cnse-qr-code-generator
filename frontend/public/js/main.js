import { onAuthChange, logout } from "./auth.js";
import { QrGenerator } from "./components/qrGenerator.js";
import { AuthModal } from "./components/authModal.js";
import { HistoryDrawer } from "./components/historyDrawer.js";
import { AuthButtons } from "./components/authButtons.js";

class App {
    constructor() {
        this.initComponents();
        this.initAuth();
    }

    initComponents() {
        // QR Generator
        this.qrGenerator = new QrGenerator(
            document.getElementById("qr-form"),
            document.getElementById("qr-image"),
            document.getElementById("qr-png-download-btn")
        );

        // Auth Modal
        this.authModal = new AuthModal(
            document.getElementById("auth-modal")
        );

        // History Drawer
        this.historyDrawer = new HistoryDrawer(
            document.getElementById("history-drawer"),
            document.getElementById("drawer-overlay"),
            (text) => this.qrGenerator.setTextAndGenerate(text)
        );

        // Auth Buttons
        this.authButtons = new AuthButtons(
            document.querySelector(".account-btns"),
            () => this.authModal.open("login"),
            () => this.authModal.open("register"),
            async () => {
                await logout();
            },
            () => this.historyDrawer.open()
        );
    }

    initAuth() {
        onAuthChange(user => {
            if (user) {
                this.authButtons.showLoggedIn(user);
                console.log("Eingeloggt:", user.email);
            } else {
                this.authButtons.showLoggedOut();
                console.log("Nicht eingeloggt");
            }
        });
    }
}

// App starten
document.addEventListener("DOMContentLoaded", () => {
    new App();
});