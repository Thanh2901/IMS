import React, { useEffect, useState } from "react";
import { Box } from "@chakra-ui/react";
import { useDisclosure } from "@chakra-ui/react";
import {
  useGetCamerasQuery,
  useGetSchedulingByCameraQuery,
  useGetSchedulingByFilterQuery,
} from "../../redux/service/camera";
import { CameraFilterBar } from "../../component/monitor/CameraFilterBar";
import { SchedulingListView } from "../../component/monitor/SchedulingListView";
import { SchedulingTableView } from "../../component/monitor/SchedulingTableView";
import { AddSchedulingModal } from "../../component/monitor/AddSchedulingModal";
import { Scheduling } from "../../type/models";
import { formatDateTimeScheduling } from "../../type/utils";

const CameraDashboard: React.FC = () => {
  const [cameraId, setCameraId] = useState<string>("");
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [filterDate, setFilterDate] = useState<string>("");
  const [view, setView] = useState<"list" | "table">("list");
  const { isOpen, onOpen, onClose } = useDisclosure();

  const { data: cameras = [] } = useGetCamerasQuery();
  const { data: schedulingByCam = [] } = useGetSchedulingByCameraQuery(cameraId, { skip: !cameraId });
  const { data: schedulingByFilter = [] } = useGetSchedulingByFilterQuery(
    { params: {startTime: formatDateTimeScheduling(startDate), endTime: formatDateTimeScheduling(endDate), cameraId} },
    { skip: !(cameraId && startDate && endDate) }
  );

  const [schedules, setSchedules] = useState<Scheduling[]>([]);

  useEffect(() => {
    if (cameraId && startDate && endDate) {
      setSchedules(schedulingByFilter);
    } else if (cameraId) {
      setSchedules(schedulingByCam);
    }
  }, [cameraId, startDate, endDate, schedulingByCam, schedulingByFilter]);

  useEffect(() => {
    if (!cameraId && cameras.length > 0) {
      setCameraId(cameras[0].id);
    }
  }, [cameras]);

  const handleSwitchView = () => {
    setView((prev) => (prev === "list" ? "table" : "list"));
  };

  const filteredSchedules = schedules.filter((item) =>
    filterDate ? item.startTime.startsWith(filterDate) : true
  );

  return (
    <Box p={2}>
      <CameraFilterBar
        cameras={cameras}
        cameraId={cameraId}
        startDate={startDate}
        endDate={endDate}
        filterDate={filterDate}
        onChangeCamera={setCameraId}
        onChangeStartDate={setStartDate}
        onChangeEndDate={setEndDate}
        onChangeFilterDate={setFilterDate}
        onAddNewClick={onOpen}
        view={view}
        onSwitchView={handleSwitchView}
      />
      {view === "list" ? (
        <SchedulingListView schedules={filteredSchedules} />
      ) : (
        <SchedulingTableView schedules={filteredSchedules} />
      )}
      <AddSchedulingModal isOpen={isOpen} onClose={onClose} cameraId={cameraId} />
    </Box>
  );
};

export default CameraDashboard;
