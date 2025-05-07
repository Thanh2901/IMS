import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQuery, transformApiResponse } from ".";
import { ApiResponse, Notification } from "../../type/models";

export const notificationApi = createApi({
  reducerPath: "notificationApi",
  baseQuery: baseQuery,
  tagTypes: ["Notifications"],
  endpoints: (builder) => ({

    getNotifications: builder.query<Notification[], void>({
      query: () => ({
        url: `/notifications`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<Notification[]>) => 
        transformApiResponse<Notification[]>(response),
      providesTags: ["Notifications"]
    }),

    getNotification: builder.query<Notification, string>({
      query: (notificationId) => ({
        url: `/notifications/${notificationId}`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<Notification>) => 
        transformApiResponse<Notification>(response),
    }),

    readNotification: builder.mutation<Notification, string>({
      query: (notificationId) => ({
        url: `/notifications/read/${notificationId}`,
        method: "PATCH"
      }),
      transformResponse: (response: ApiResponse<Notification>) =>
        transformApiResponse<Notification>(response),
      invalidatesTags: ["Notifications"]
    }),
  }),
});

export const {
  useGetNotificationsQuery,
  useReadNotificationMutation,
  useGetNotificationQuery
} = notificationApi;