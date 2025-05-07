export interface AppConfig {
  baseUrl: string
};

export const appConfig: AppConfig = {
  baseUrl: import.meta.env.VITE_API_BASE_URL || "http://localhost:8000/api",
};

// TODO: use env
export const firebaseConfig = {
  apiKey: "AIzaSyCWjOwZfPDeBiIeivARr4fsMZ_1p_F-Wh0",
  authDomain: "notification-service-c556c.firebaseapp.com",
  projectId: "notification-service-c556c",
  storageBucket: "notification-service-c556c.appspot.com",
  messagingSenderId: "543455414606",
  appId: "1:543455414606:web:7242afe8f36c0a21d8b61f",
  measurementId: "G-34XK4MD4C8"
};