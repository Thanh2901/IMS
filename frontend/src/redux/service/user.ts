import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQuery, transformApiResponse } from ".";
import { ApiResponse, User, UserUpdateMeRequest } from "../../type/models";

interface TDataUserUpdateMe {
  requestBody: UserUpdateMeRequest;
};

export const userApi = createApi({
  reducerPath: "usersApi",
  baseQuery: baseQuery,
  tagTypes: ["Users", "Me"],
  endpoints: (builder) => ({

    getMe: builder.query<User, void>({
      query: () => ({
        url: "/users/me",
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<User>) => 
        transformApiResponse<User>(response),
      providesTags: ["Me"],
    }),

    updateMe: builder.mutation<User, TDataUserUpdateMe>({
      query: ({ requestBody }) => ({
        url: "/users/me",
        method: "PATCH",
        body: requestBody
      }),
      transformResponse: (response: ApiResponse<User>) => 
        transformApiResponse<User>(response),
      invalidatesTags: ["Me"]
    })
  }),
});

export const {
  useGetMeQuery,
  useUpdateMeMutation,
} = userApi;