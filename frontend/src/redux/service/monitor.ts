import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQuery } from ".";

export const monitorApi = createApi({
    reducerPath: "monitorApi",
    baseQuery: baseQuery,
    tagTypes: ["Camera", "Scheduling"],
    endpoints: (builder) => ({

        // Xóa scheduling
        deleteScheduling: builder.mutation<string, string>({
            query: (id) => ({
                url: `/cameras/scheduling/delete/${id}`,
                method: "DELETE",
            }),
            invalidatesTags: ["Scheduling"],
        }),
    }),
});

// Export hooks để sử dụng trong components
export const {
    useDeleteSchedulingMutation,
} = monitorApi;