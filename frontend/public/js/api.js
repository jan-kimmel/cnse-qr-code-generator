export async function createQrCode(text, token) {
    const headers = { "Content-Type": "application/json" };
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch("/api/qr-codes", {
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
    const response = await fetch("/api/qr-codes/history", {
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
    return `/api/qr-codes/${id}/image`;
}

export function getQrCodeDownloadUrl(id) {
    return `/api/qr-codes/${id}/download`;
}