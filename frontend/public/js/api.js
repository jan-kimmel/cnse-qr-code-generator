const API_BASE_URL = "http://192.168.178.40:8080/api/qr-codes";

export async function createQrCode(text, token) {
    const headers = { "Content-Type": "application/json" };
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(API_BASE_URL, {
        method: "POST",
        headers,
        body: JSON.stringify({ text })
    });

    if (!response.ok) {
        throw new Error("Failed to create QR code");
    }

    return response.json();
}

export async function getQrCodeHistory(token) {
    const response = await fetch(`${API_BASE_URL}/history`, {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error("Failed to fetch history");
    }

    return response.json();
}

export function getQrCodeImageUrl(id) {
    return `${API_BASE_URL}/${id}/image`;
}

export function getQrCodeDownloadUrl(id) {
    return `${API_BASE_URL}/${id}/download`;
}