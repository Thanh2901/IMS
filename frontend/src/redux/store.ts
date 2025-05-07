import { configureStore } from "@reduxjs/toolkit";
import { authApi } from "./service/auth";
import { setupListeners } from "@reduxjs/toolkit/query";
import { userApi } from "./service/user";
import { infrastructureApi } from "./service/infrastructure";
import { reportApi } from "./service/report";
import { notificationApi } from "./service/notification";
import { cameraApi } from "./service/camera";

export const store = configureStore({
  reducer: {
    [authApi.reducerPath]: authApi.reducer,
    [userApi.reducerPath]: userApi.reducer,
    [infrastructureApi.reducerPath]: infrastructureApi.reducer,
    [reportApi.reducerPath]: reportApi.reducer,
    [notificationApi.reducerPath]: notificationApi.reducer,
    [cameraApi.reducerPath]: cameraApi.reducer,
  },
  middleware: (getDefaultMiddleware) => {
    return getDefaultMiddleware()
      .concat(authApi.middleware)
      .concat(userApi.middleware)
      .concat(infrastructureApi.middleware)
      .concat(reportApi.middleware)
      .concat(notificationApi.middleware)
      .concat(cameraApi.middleware)
  }
});

setupListeners(store.dispatch);