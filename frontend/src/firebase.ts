import { initializeApp } from "firebase/app";
import { firebaseConfig } from "./config";
import { getToken, MessagePayload, onMessage } from "firebase/messaging";
import { getMessaging } from "firebase/messaging/sw";

const app = initializeApp(firebaseConfig);

export const messaging = getMessaging(app);

export const requestToken = async (setFcmToken: any) => {
  try {
    const permission = await Notification.requestPermission();
    if (permission === "granted") {
      const token = await getToken(messaging);
      if (token) {
        console.log("FCM Token: ", token);
        setFcmToken(token);
      } else {
        console.log("No registration token available. Request permission to get one.");
      }
    } else {
      console.log("Notification permission not granted.");
    }
  } catch (error) {
    console.error("An error occurred while retrieving token: ", error);
  }
};

export const onMessageListener = (): Promise<MessagePayload> => {
  return new Promise((resolve) => {
    onMessage(messaging, (payload) => {
      resolve(payload);
    });
  });
};