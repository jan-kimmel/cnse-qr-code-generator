import { initializeApp } from "https://www.gstatic.com/firebasejs/12.7.0/firebase-app.js";
import { getAuth } from "https://www.gstatic.com/firebasejs/12.7.0/firebase-auth.js";

const firebaseConfig = {
    apiKey: "AIzaSyAf7pivQ3VLeGe35ufcFZGAm7NLVJwLjNA",
    authDomain: "cnse-qr-code-generator.firebaseapp.com",
    projectId: "cnse-qr-code-generator",
    storageBucket: "cnse-qr-code-generator.firebasestorage.app",
    messagingSenderId: "162846799968",
    appId: "1:162846799968:web:cfc418fd82e99c18a82b05"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

export { auth };
