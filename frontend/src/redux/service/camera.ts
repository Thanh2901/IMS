import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQuery, transformApiResponse } from ".";
import { ApiResponse, Camera, CreateCameraRequest, DataCreateScheduling, Scheduling, UpdateCameraRequest } from "../../type/models";

interface TDataCreateCamera {
  requestBody: CreateCameraRequest;
};

interface TDataUpdateCamera {
  cameraId: string;
  requestBody: UpdateCameraRequest;
};

interface TDataCreateScheduling {
  requestBody: DataCreateScheduling;
};

interface TParamsFilterScheduling {
  params: DataCreateScheduling
};

export const cameraApi = createApi({
  reducerPath: "cameraApi",
  baseQuery: baseQuery,
  tagTypes: ["Cameras", "Scheduling"],
  endpoints: (builder) => ({

    getCamera: builder.query<Camera, string>({
      query: (cameraId) => ({
        url: `/cameras/get/${cameraId}`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<Camera>) =>
        transformApiResponse<Camera>(response),
    }),

    getCameraStreaming: builder.query<string, string>({
      query: (cameraId) => ({
        url: `/cameras/streaming/${cameraId}`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<string>) =>
        transformApiResponse<string>(response),
    }),

    createCamera: builder.mutation<Camera, TDataCreateCamera>({
      query: ({ requestBody }) => ({
        url: `/cameras/create`,
        method: "POST",
        body: requestBody
      }),
      transformResponse: (response: ApiResponse<Camera>) =>
        transformApiResponse<Camera>(response),
      invalidatesTags: ["Cameras"]
    }),

    updateCamera: builder.mutation<Camera, TDataUpdateCamera>({
      query: ({ cameraId, requestBody }) => ({
        url: `/cameras/update/${cameraId}`,
        method: "POST",
        body: requestBody
      }),
      transformResponse: (response: ApiResponse<Camera>) =>
        transformApiResponse<Camera>(response),
      invalidatesTags: ["Cameras"]
    }),

    getCameras: builder.query<Camera[], void>({
      query: () => ({
        url: `/cameras/me`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<Camera[]>) =>
        transformApiResponse<Camera[]>(response),
      providesTags: ["Cameras"],
    }),

    getSchedulingByCamera: builder.query<Scheduling[], string>({
      query: (cameraId) => ({
        url: `/cameras/scheduling/camera/${cameraId}`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<Scheduling[]>) =>
        transformApiResponse<Scheduling[]>(response),
      providesTags: ["Scheduling"]
    }),

    addScheduling: builder.mutation<Scheduling, TDataCreateScheduling>({
      query: ({ requestBody }) => ({
        url: `/cameras/scheduling`,
        method: "POST",
        body: requestBody
      }),
      transformResponse: (response: ApiResponse<Scheduling>) =>
        transformApiResponse<Scheduling>(response),
      invalidatesTags: ["Scheduling"]
    }),

    getSchedulingByFilter: builder.query<Scheduling[], TParamsFilterScheduling>({
      query: ({ params }) => ({
        url: `/cameras/scheduling/filter/time`,
        method: "GET",
        params: params
      }),
      transformResponse: (response: ApiResponse<Scheduling[]>) =>
        transformApiResponse<Scheduling[]>(response),
      providesTags: ["Scheduling"]
    }),

    getSchedule: builder.query<Scheduling, string>({
      query: (id) => ({
        url: `/cameras/scheduling/${id}`,
        method: "GET"
      })
    }),

    getSchedules: builder.query<Scheduling[], void>({
      query: () => ({
        url: `/cameras/scheduling/all`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<Scheduling[]>) =>
        transformApiResponse<Scheduling[]>(response),
      providesTags: ["Scheduling"],
    }),

    createSchedule: builder.mutation<Scheduling, void>({
      query: () => ({
        url: `/cameras/scheduling`,
        method: "POST",
      }),
      transformResponse: (response: ApiResponse<Scheduling>) =>
        transformApiResponse<Scheduling>(response),
      invalidatesTags: ["Scheduling"]
    }),

  }),
});

export const {
  useGetCamerasQuery,
  useGetSchedulingByCameraQuery,
  useAddSchedulingMutation,
  useGetSchedulingByFilterQuery,
  useGetCameraQuery,
  useCreateCameraMutation,
  useUpdateCameraMutation,
  useGetScheduleQuery,
  useGetCameraStreamingQuery
} = cameraApi;