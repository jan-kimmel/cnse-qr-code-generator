import {
    signInWithEmailAndPassword,
    createUserWithEmailAndPassword,
    signOut,
    onAuthStateChanged
} from "https://www.gstatic.com/firebasejs/12.7.0/firebase-auth.js";

import { auth } from "./firebase.js";

export function login(email, password) {
    return signInWithEmailAndPassword(auth, email, password);
}

export function register(email, password) {
    return createUserWithEmailAndPassword(auth, email, password);
}

export function logout() {
    return signOut(auth);
}

export function onAuthChange(callback) {
    onAuthStateChanged(auth, callback);
}

export async function getIdToken() {
    if (!auth.currentUser) return null;
    return auth.currentUser.getIdToken();
}
