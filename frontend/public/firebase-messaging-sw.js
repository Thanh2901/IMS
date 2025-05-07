// Scripts for firebase and firebase messaging
importScripts('https://www.gstatic.com/firebasejs/8.2.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.2.0/firebase-messaging.js');

// Initialize the Firebase app in the service worker by passing the generated config
const firebaseConfig = {
  apiKey: "AIzaSyCWjOwZfPDeBiIeivARr4fsMZ_1p_F-Wh0",
  authDomain: "notification-service-c556c.firebaseapp.com",
  projectId: "notification-service-c556c",
  storageBucket: "notification-service-c556c.appspot.com",
  messagingSenderId: "543455414606",
  appId: "1:543455414606:web:7242afe8f36c0a21d8b61f",
  measurementId: "G-34XK4MD4C8"
};
firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function (payload) {
  console.log('Received background message ', payload);
  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
  };

  self.registration.showNotification(notificationTitle,
    notificationOptions);

  self.clients.matchAll({ includeUncontrolled: true }).then(clients => {
    clients.forEach(client => {
      client.postMessage({
        type: 'BACKGROUND_MESSAGE_RECEIVED',
        payload: payload,
      });
    });
  });
});