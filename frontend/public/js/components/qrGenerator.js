import { getIdToken } from "../auth.js";
import { createQrCode, getQrCodeImageUrl, getQrCodeDownloadUrl } from "../api.js";

export class QrGenerator {
    constructor(formElement, imageElement, downloadButton) {
        this.form = formElement;
        this.textInput = formElement.querySelector("#qr-text");
        this.image = imageElement;
        this.downloadButton = downloadButton;
        this.currentQrId = null;

        this.init();
    }

    init() {
        this.textInput.focus();
        this.form.addEventListener("submit", (e) => this.handleSubmit(e));
        this.downloadButton.addEventListener("click", () => this.handleDownload());
    }

    async handleSubmit(event) {
        event.preventDefault();

        const rawText = this.textInput.value.trim();
        if (!rawText) return;
        const text = this.normalizeUrl(rawText);

        try {
            const token = await getIdToken();
            const data = await createQrCode(text, token);

            this.currentQrId = data.id;
            this.showQrCode(data.id);
        } catch (error) {
            console.error("Error creating QR code:", error);
            alert("Fehler beim Erstellen des QR-Codes");
        }
    }

    normalizeUrl(text) {
        try {
            const url = new URL(text);
            return url.href;
        } catch {
            return text;
        }
    }

    showQrCode(id) {
        this.image.src = getQrCodeImageUrl(id);
        this.image.classList.remove("hidden");
        this.downloadButton.disabled = false;
        this.downloadButton.classList.remove("button-disabled");
    }

    handleDownload() {
        if (!this.currentQrId) return;

        const downloadUrl = getQrCodeDownloadUrl(this.currentQrId);
        const link = document.createElement("a");
        link.href = downloadUrl;
        link.download = `qrcode-${this.currentQrId}`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    setTextAndGenerate(text) {
        this.textInput.value = text;
        this.form.requestSubmit();
    }
}