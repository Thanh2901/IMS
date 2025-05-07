import { createApi } from "@reduxjs/toolkit/query/react";
import { Token, RegisterRequest, LoginRequest, User, ApiResponse } from "../../type/models";
import { baseQuery } from ".";
import { transformApiResponse } from ".";

interface TDataRegister {
  requestBody: RegisterRequest;
};

interface TDataLogin {
  requestBody: LoginRequest;
};

interface ForgotPasswordQueryParams {
  email: string;
};

interface ResetPasswordQueryParams {
  token: string;
  newPassword: string;
};
export const authApi = createApi({
  reducerPath: "authApi",
  baseQuery: baseQuery,
  tagTypes: ["Auth"],
  endpoints: (builder) => ({
    register: builder.mutation<User, TDataRegister>({
      query: ({ requestBody }) => ({
        url: "/auth/register",
        method: "POST",
        body: requestBody,
      }),
      transformResponse: (response: ApiResponse<User>) =>
        transformApiResponse<User>(response),
    }),

    login: builder.mutation<Token, TDataLogin>({
      query: ({ requestBody }) => ({
        url: "/auth/login",
        method: "POST",
        body: requestBody,
      }),
      invalidatesTags: ["Auth"],
      transformResponse: (response: ApiResponse<Token>) =>
        transformApiResponse<Token>(response),
    }),

    logout: builder.mutation<string, void>({
      query: () => ({
        url: "/auth/logout",
        method: "POST",
      }),
      invalidatesTags: ["Auth"],
      transformResponse: (response: ApiResponse<string>) =>
        transformApiResponse<string>(response),
    }),

    forgotPassword: builder.mutation<string, ForgotPasswordQueryParams>({
      query: (params) => ({
        url: "/auth/forgot-password",
        method: "POST",
        params,
      }),
      transformResponse: (response: ApiResponse<string>) => 
        transformApiResponse<string>(response),
    }),

    resetPassword: builder.mutation<string, ResetPasswordQueryParams>({
      query: (params) => ({
        url: "/auth/reset-password",
        method: "POST",
        params
      }),
      transformResponse: (response: ApiResponse<string>) => 
        transformApiResponse<string>(response),
    }),

  }),
});

export const {
  useRegisterMutation,
  useLoginMutation,
  useLogoutMutation,
  useForgotPasswordMutation,
  useResetPasswordMutation
} = authApi;
