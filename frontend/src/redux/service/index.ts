import { fetchBaseQuery } from "@reduxjs/toolkit/query";
import { appConfig } from "../../config";
import { ApiResponse } from "../../type/models";

export const baseQuery = fetchBaseQuery({
  baseUrl: appConfig.baseUrl,
  prepareHeaders: (headers) => {
    const token = localStorage.getItem("access_token");
    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }
    //headers.set("Content-Type", "application/json");
    return headers;
  },
});

// pre transform api response to extract main data field
export const transformApiResponse = <T>(response: ApiResponse<T>): T => {
  console.log(response);
  if (response.code !== 1000) {
    throw new Error(response.message);
  }
  return response.data as T;
};
