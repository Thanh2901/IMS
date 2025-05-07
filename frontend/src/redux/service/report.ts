import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQuery, transformApiResponse } from ".";
import { ApiResponse, ReportGenerateRequest } from "../../type/models";

interface TDataGetReportByCamera {
  requestBody: ReportGenerateRequest;
};

export const reportApi = createApi({
  reducerPath: "reportsApi",
  baseQuery: baseQuery,
  tagTypes: ["Reports"],
  endpoints: (builder) => ({

    generateReportByCamera: builder.mutation<string, TDataGetReportByCamera>({
      query: ({requestBody}) => ({
        url: "/reports/camera",
        method: "POST",
        body: requestBody
      }),
      // transformResponse: (response: ApiResponse<string>) => 
      //   transformApiResponse<string>(response),
    }),

    generateReportByInfraObject: builder.mutation<string, string>({
      query: (infraId) => ({
        url: `/reports/infra/${infraId}`,
        method: "GET"
      }),
      // transformResponse: (response: ApiResponse<string>) => 
      //   transformApiResponse<string>(response),
    }),

  }),
});

export const {
  useGenerateReportByCameraMutation,
  useGenerateReportByInfraObjectMutation,
} = reportApi;