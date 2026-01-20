import { getIdToken } from "../auth.js";
import { getQrCodeHistory } from "../api.js";

export class HistoryDrawer {
    constructor(drawerElement, overlayElement, onTextSelect) {
        this.drawer = drawerElement;
        this.overlay = overlayElement;
        this.list = drawerElement.querySelector("#history-list");
        this.closeButton = drawerElement.querySelector("#close-history");
        this.onTextSelect = onTextSelect;

        this.init();
    }

    init() {
        this.closeButton.addEventListener("click", () => this.close());
        this.overlay.addEventListener("click", () => this.close());
    }

    async open() {
        this.drawer.classList.add("open");
        this.drawer.classList.remove("hidden");
        this.overlay.classList.remove("hidden");

        try {
            const token = await getIdToken();
            if (!token) {
                alert("Bitte einloggen");
                this.close();
                return;
            }

            const history = await getQrCodeHistory(token);
            this.renderHistory(history);
        } catch (error) {
            console.error("Error fetching history:", error);
            alert("Fehler beim Laden des Verlaufs");
            this.close();
        }
    }

    close() {
        this.drawer.classList.remove("open");
        this.overlay.classList.add("hidden");
        setTimeout(() => this.drawer.classList.add("hidden"), 300);
    }

    renderHistory(history) {
        this.list.innerHTML = "";

        const texts = history?.texts || [];

        if (texts.length === 0) {
            this.list.innerHTML = "<span class='empty'>Noch kein Verlauf</span>";
            return;
        }

        texts.forEach(text => {
            const li = document.createElement("li");
            li.textContent = text;
            li.onclick = () => {
                this.close();
                this.onTextSelect(text);
            };
            this.list.appendChild(li);
        });
    }
}