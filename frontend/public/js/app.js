const form = document.getElementById("qr-form");
const input = document.getElementById("qr-text");
const result = document.getElementById("qr-image");
const image = document.getElementById("qr-image");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const response = await fetch("http://localhost:8080/api/qrcodes", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({text: input.value})
    });

    if (!response.ok) {
        alert("Fehler beim Erstellen des QR-Codes");
        return;
    }

    const data = await response.json();
    image.src = data.imageURL;
    result.hidden = false;
})