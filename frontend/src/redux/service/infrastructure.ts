import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQuery, transformApiResponse } from ".";
import { ApiResponse, Event, EventFilterRequest, ObjectFilterRequest, InfraObject, PageResponse, Statistic, SearchObjectsParams, StatisticParams, FakeEvent, UpdateObjectRequest, History, InfraObjectProcess, FilterProcessRequests } from "../../type/models";

interface TDataInfraFilter {
  requestBody: ObjectFilterRequest;
};

interface TDataEventFilter {
  requestBody: EventFilterRequest;
};

interface TParamsStatistic {
  params: StatisticParams;
}

interface TDataSearchObjects {
  params: SearchObjectsParams;
};

interface TDataAddObject {
  formData: FormData;
};

interface TDataUpdateObject {
  formData: FormData;
};

interface TParamsFilterProcess {
  params: FilterProcessRequests;
};


export const infrastructureApi = createApi({
  reducerPath: "infrastructuresApi",
  baseQuery: baseQuery,
  tagTypes: ["Objects", "Events", "FakeEvents", "Processes"],
  endpoints: (builder) => ({

    getObjects: builder.query<PageResponse<InfraObject>, TDataInfraFilter>({
      query: ({ requestBody }) => ({
        url: "/infrastructures/filter",
        method: "POST",
        body: requestBody
      }),
      transformResponse: (response: ApiResponse<PageResponse<InfraObject>>) =>
        transformApiResponse<PageResponse<InfraObject>>(response),
      providesTags: ["Objects"]
    }),

    getObjectById: builder.query<InfraObject, string>({
      query: (objectId) => ({
        url: `/infrastructures/${objectId}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<InfraObject>) =>
        transformApiResponse<InfraObject>(response),
    }),

    getEventById: builder.query<Event, string>({
      query: (eventId) => ({
        url: `/infrastructures/events/${eventId}`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<Event>) =>
        transformApiResponse<Event>(response),
    }),

    getEvents: builder.query<PageResponse<Event>, TDataEventFilter>({
      query: ({ requestBody }) => ({
        url: "/infrastructures/events/filter",
        method: "POST",
        body: requestBody
      }),
      transformResponse: (response: ApiResponse<PageResponse<Event>>) => 
        transformApiResponse<PageResponse<Event>>(response),
      providesTags: ["Events"]
    }),

    getEventsByObjectId: builder.query<Event[], string>({
      query: (objectId) => ({
        url: `/infrastructures/events/infra/${objectId}`,
        method: "GET"
      }),
      transformResponse: (response: ApiResponse<Event[]>) => 
        transformApiResponse<Event[]>(response),
      providesTags: (result, error, objectId) => [{type: "Events", id: objectId}],
    }),

    getStatistics: builder.query<Statistic, TParamsStatistic>({
      query: ({ params }) => ({
        url: "/infrastructures/statistics",
        method: "GET",
        params: params
      }),
      transformResponse: (response: ApiResponse<Statistic>) =>
        transformApiResponse<Statistic>(response),
      providesTags: ["Events"],
    }),

    searchObjects: builder.query<PageResponse<InfraObject>, TDataSearchObjects>({
      query: ({params}) => ({
        url: "/infrastructures/search",
        method: "GET",
        params: params
      }),
      transformResponse: (response: ApiResponse<PageResponse<InfraObject>>) =>
        transformApiResponse<PageResponse<InfraObject>>(response),
    }),

    markFakeEvent: builder.mutation<FakeEvent, string>({
      query: (eventId) => ({
        url: `/infrastructures/events/fake/${eventId}`,
        method: "PATCH"
      }),
      transformResponse: (response: ApiResponse<FakeEvent>) =>
        transformApiResponse<FakeEvent>(response),
    }),

    getFakeEvents: builder.query<FakeEvent[], void>({
      query: () => ({
        url: "/infrastructures/events/fake",
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<FakeEvent[]>) => 
        transformApiResponse<FakeEvent[]>(response),
      providesTags: ["FakeEvents"]
    }),

    deleteFakeEvent: builder.mutation<void, string>({
      query: (eventId) => ({
        url: `/infrastructures/events/fake/${eventId}`,
        method: "DELETE"
      }),
      invalidatesTags: ["FakeEvents"]
    }),

    createRepairEvent: builder.mutation<Event, string>({
      query: (objectId) => ({
        url: `/infrastructures/events/repair/${objectId}`,
        method: "POST",
      }),
      transformResponse: (response: ApiResponse<Event>) => 
        transformApiResponse<Event>(response),
      invalidatesTags: (result, error, objectId) => [{type: "Events", id: objectId}]
    }),

    updateRepairEvent: builder.mutation<Event, string>({
      query: (objectId) => ({
        url: `/infrastructures/events/repair/${objectId}`,
        method: "PATCH",
      }),
      transformResponse: (response: ApiResponse<Event>) => 
        transformApiResponse<Event>(response),
      invalidatesTags: (result, error, objectId) => [{type: "Events", id: objectId}]
    }),

    addInfrastructureObject: builder.mutation<InfraObject, TDataAddObject>({
      query: ({ formData }) => ({
        url: `/infrastructures`,
        method: "POST",
        body: formData
      }),
      transformResponse: (response: ApiResponse<InfraObject>) => 
        transformApiResponse<InfraObject>(response),
      invalidatesTags: ["Objects"]
    }),

    updateInfrastructureObject: builder.mutation<InfraObject, TDataUpdateObject>({
      query: ({ formData }) => ({
        url: "/infrastructures",
        method: "PATCH",
        body: formData,
      }),
      transformResponse: (response: ApiResponse<InfraObject>) =>
        transformApiResponse<InfraObject>(response),
      invalidatesTags: ["Objects"]
    }),

    deleteInfrastructureObject: builder.mutation<void, string>({
      query: (objectId) => ({
        url: `/infrastructures/${objectId}`,
        method: "DELETE"
      }),
      invalidatesTags: ["Objects"]
    }),

    getHistoryByInfraId: builder.query<History[], string>({
      query: (infraId) => ({
        url: `/infrastructures/history/infra/${infraId}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<History[]>) =>
        transformApiResponse<History[]>(response),
    }),

    getProcessFilter: builder.query<InfraObjectProcess[], TParamsFilterProcess>({
      query: ({ params }) => ({
        url: `/infrastructures/process/schedule`,
        method: "GET",
        params: params
      }),
      transformResponse: (response: ApiResponse<InfraObjectProcess[]>) =>
        transformApiResponse<InfraObjectProcess[]>(response),
      providesTags: ["Processes"]
    }),

    rejectProcess: builder.mutation<InfraObjectProcess, string>({
      query: (processId) => ({
        url: `/infrastructures/process/reject/${processId}`,
        method: "PATCH",
      }),
      transformResponse: (response: ApiResponse<InfraObjectProcess>) =>
        transformApiResponse<InfraObjectProcess>(response),
      invalidatesTags: ["Processes"]
    }),

    processSchedule: builder.mutation<string, string>({
      query: (scheduleId) => ({
        url: `/infrastructures/process/schedule/${scheduleId}`,
        method: "POST",
      }),
      transformResponse: (response: ApiResponse<string>) =>
        transformApiResponse<string>(response),
      invalidatesTags: ["Processes"]
    }),

    processObject: builder.mutation<InfraObjectProcess, string>({
      query: (processId) => ({
        url: `/infrastructures/process/${processId}`,
        method: "POST",
      }),
      transformResponse: (response: ApiResponse<InfraObjectProcess>) =>
        transformApiResponse<InfraObjectProcess>(response),
      invalidatesTags: (result, error, processId) => [{type: "Processes", id: processId}]
    }),


  }),
});


export const {
  useGetObjectsQuery,
  useGetEventsQuery,
  useGetStatisticsQuery,
  useSearchObjectsQuery,
  useMarkFakeEventMutation,
  useGetFakeEventsQuery,
  useDeleteFakeEventMutation,
  useCreateRepairEventMutation,
  useUpdateRepairEventMutation,
  useGetEventsByObjectIdQuery,
  useAddInfrastructureObjectMutation,
  useUpdateInfrastructureObjectMutation,
  useGetEventByIdQuery,
  useGetHistoryByInfraIdQuery,
  useGetObjectByIdQuery,
  useDeleteInfrastructureObjectMutation,
  useGetProcessFilterQuery,
  useRejectProcessMutation,
  useProcessScheduleMutation,
  useProcessObjectMutation,
} = infrastructureApi;