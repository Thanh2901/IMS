import L from "leaflet";
import { EventStatus, InfraCategory, InfraObject, InfraStatus, Notification, SchedulingStatus } from "./models";
import roadIcon from "../assets/road.png";
import signIcon from "../assets/sign.png";
import guardrailIcon from "../assets/guardrail.png";
import lampIcon from "../assets/lamp.png";

export const formatDate = (date: string): string => {
  if (date == null) { 
    return "Now";
  }

  return new Date(date).toLocaleString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
    day: "2-digit",
    month: "2-digit",
    year: "numeric"
  });
};

export const getEventStatusColor = (status: EventStatus): string => {
  switch (status) {
    case EventStatus.NEW:
      return 'green';
    case EventStatus.UPDATED:
      return 'blue';
    case EventStatus.REPAIR:
      return 'yellow';
    case EventStatus.LOST:
      return 'red';
    default:
      return 'gray';
  }
};

export const getStatusColor = (status: InfraStatus): string => {
  switch (status) {
    case InfraStatus.OK:
      return 'green';
    case InfraStatus.NOT_OK:
      return 'red';
    default:
      return 'gray';
  }
};

export const formatLatLon = (l: number): string => {
  return l.toFixed(6);
};

export const getDefaultCenter = (objects: InfraObject[]): [number, number] => {
  if (objects.length === 0) {
    return [0.0, 0.0];
  }

  const total = objects.reduce(
    (acc, object) => {
      acc.lat += object.latitude;
      acc.lon += object.longitude;
      return acc;
    },
    { lat: 0, lon: 0 }
  );

  const avgLat = total.lat / objects.length;
  const avgLon = total.lon / objects.length;

  return [avgLat, avgLon];
};

export const getObjectCategoryColor = (category: InfraCategory) => {
  switch (category) {
    case InfraCategory.SIGN:
      return "green";
    case InfraCategory.GUARDRAIL:
      return "orange";
    case InfraCategory.LAMP:
      return "blue";
    case InfraCategory.ROAD:
      return "pink";
  };
};


export const parseAdditionalData = (notification: Notification) => {
  if (!notification.additionalData) return null;
  try {
    // const pairs = notification.additionalData.substring(1, notification.additionalData.length - 1).split(',').map(pair => pair.trim());
    const pairs = notification.additionalData.substring(1, notification.additionalData.length - 1).split('=');
    console.log(pairs);
    const dataObject: Record<string, string> = {};
    let prevKey = pairs[0];
    for (let i = 1; i < pairs.length - 1; i++) {
      const splittedPair = pairs[i].split(',');
      const value = splittedPair.slice(0, -1).join(',');
      dataObject[prevKey.trim()] = value.trim();
      prevKey = splittedPair[splittedPair.length - 1];
    }
    dataObject[prevKey.trim()] = pairs[pairs.length - 1];
    //pairs.forEach(pair => {
    //  const [key, value] = pair.split('=');
    //  if (key && value) {
    //    dataObject[key.trim()] = value.trim();
    //  }
    //});
    console.log(dataObject);
    return dataObject;
  } catch (error) {
    console.error('Error parsing additional data:', error);
    return null;
  }
};

// TODO: might change this
export const getVisiblePages = (currentPage: number, totalPages: number): (number | string)[] => {
  const visiblePages: (number | string)[] = [];
  const maxVisiblePages = 5;

  if (totalPages <= maxVisiblePages) {
    return Array.from({ length: totalPages }, (_, i) => i);
  }

  const halfVisible = Math.floor(maxVisiblePages / 2);
  let startPage = Math.max(0, currentPage - halfVisible);
  let endPage = Math.min(totalPages - 1, currentPage + halfVisible);

  if (currentPage <= halfVisible) {
    startPage = 0;
    endPage = maxVisiblePages - 1;
  } else if (currentPage >= totalPages - halfVisible - 1) {
    startPage = totalPages - maxVisiblePages;
    endPage = totalPages - 1;
  }

  if (startPage > 0) {
    visiblePages.push(0);
    if (startPage > 1) {
      visiblePages.push('start-ellipsis');
    }
  }

  for (let i = startPage; i <= endPage; i++) {
    visiblePages.push(i);
  }

  if (endPage < totalPages - 1) {
    if (endPage < totalPages - 2) {
      visiblePages.push('end-ellipsis');
    }
    visiblePages.push(totalPages - 1);
  }

  return visiblePages;
};

export const getCustomIcon = (name: InfraCategory) => {
  const colorMap = {
    "SIGN": signIcon,
    "ROAD": roadIcon,
    "LAMP": lampIcon,
    "GUARDRAIL": guardrailIcon
  };

  const icon = colorMap[name] || "black";
  return new L.Icon({
    // iconUrl: `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-${color}.png`,
    iconUrl: icon,
    iconSize: [20, 31],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    // shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
    // shadowSize: [41, 41]
  });
};

export const getObjectsAmount = (objects: InfraObject[]): Map<string, number> => {
  const map = new Map<string, number>();
  objects.forEach(obj => {
    map.set(obj.category, map.get(obj.category) || 0 + 1);
  });
  return map;
};

export const formatConfidence = (confidence: number) => {
  return (confidence * 100).toFixed(2);
};

export const formatDateTimeScheduling = (dateTimeStr: string) => {
  const date = new Date(dateTimeStr);
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const min = String(date.getMinutes()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
};

export const validateDates = (startDate: string | undefined, endDate: string | undefined) => {
  if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
    return "Start date cannot be after end date";
  }
  return true;
};

export const capitalizeFirstLetter = (s: string): string => {
  return String(s).charAt(0).toUpperCase() + String(s).slice(1);
};

export const getSchedulingStatusColor = (status: SchedulingStatus): string => {
  switch (status) {
    case "PENDING":
      return "yellow";
    case "RUNNING":
      return "blue";
    case "DONE":
      return "green";
    case "FAILED":
      return "purple";
    default:
      return "gray";
  }
};