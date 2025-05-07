import { Route, Routes } from "react-router-dom";
import Login from "./page/auth/Login";
import Register from "./page/auth/Register";
import NotFound from "./page/NotFound";
import Dashboard from "./page/dashboard/Dashboard";
import Layout from "./page/Layout";
import { useEffect, useState } from "react";
import { onMessageListener, requestToken } from "./firebase";
import Home from "./page/Home";
import MapPage from "./page/map/MapPage";
import useCustomToast from "./hook/useCustomToast";
import { MessagePayload } from "firebase/messaging";
import NotificationDetails from "./page/notification/NotificationDetails";
import { useDispatch } from "react-redux";
import { notificationApi } from "./redux/service/notification";
import Events from "./page/events/Events";
import { infrastructureApi } from "./redux/service/infrastructure";
import ObjectsPage from "./page/objects/Objects";
import Report from "./page/report/Report";
import CameraDashboard from "./page/monitor/MonitorPage.tsx";
import EventDetails from "./page/events/EventDetails";
import InfraJsonViewer from "./component/monitor/InfraJsonViewer.tsx";
import CameraManagementPage from "./page/cameras/CameraManagementPage.tsx";
import FakeEvents from "./page/fake_events/FakeEvents.tsx";
import ObjectDetails from "./page/objects/ObjectDetails.tsx";
import ProcessPage from "./page/monitor/ProcessPage.tsx";

export default function App() {
  const [fcmToken, setFcmToken] = useState<string | null>(null);
  const showToast = useCustomToast();
  const dispatch = useDispatch();

  useEffect(() => {
    requestToken(setFcmToken);
    onMessageListener()
      .then((payload: MessagePayload) => {
        console.log("Received FCM message: ", payload);

        // invalidate cache tags to trigger refetch
        dispatch(notificationApi.util.invalidateTags(["Notifications"]));
        dispatch(infrastructureApi.util.invalidateTags(["Objects", "Events"]));

        showToast(
          payload?.notification?.title as string,
          payload?.notification?.body as string,
          "success"
        );
      })
      .catch((err) => console.error("Failed to handle FCM message: ", err));

    navigator?.serviceWorker?.addEventListener("message", (event) => {
      if (event.data?.type === "BACKGROUND_MESSAGE_RECEIVED") {
        console.log("Background message received:", event.data.payload);
        dispatch(notificationApi.util.invalidateTags(["Notifications"]));
        dispatch(infrastructureApi.util.invalidateTags(["Objects", "Events"]));
      }
    });
  }, []);

  return (
    <>
      <Routes>
        <Route path="/login" element={<Login fcmToken={fcmToken} />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Home />} />

        <Route element={<Layout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/map" element={<MapPage />} />
          <Route path="/objects" element={<ObjectsPage />} />
          <Route path="/objects/:objectId" element={<ObjectDetails />} />
          <Route path={"/monitor"} element={<CameraDashboard />} />
          <Route path="/process/:scheduleId" element={<ProcessPage />} />
          <Route path="/notifications/:notificationId" element={<NotificationDetails />} />
          <Route path="/events" element={<Events />} />
          <Route path="/events/:eventId" element={<EventDetails />} />
          <Route path="/report" element={<Report />} />
          <Route path="/schedule/:scheduleId" element={<InfraJsonViewer />} />
          <Route path="/cameras" element={<CameraManagementPage />} />
          <Route path="/fake-events" element={<FakeEvents />} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </>
  );
}
