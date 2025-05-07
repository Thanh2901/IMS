import React, { useEffect, useRef, useState } from "react";
import { Box, useDisclosure } from "@chakra-ui/react";
import { MapContainer, Marker, TileLayer, LayersControl, useMap, useMapEvent } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import { InfraObject } from "../../type/models";
import ObjectPopup from "./ObjectPopup";
import L from "leaflet";
import "leaflet-gesture-handling";
import "leaflet-gesture-handling/dist/leaflet-gesture-handling.css";
import "leaflet-fullscreen/dist/leaflet.fullscreen.css";
import "leaflet-fullscreen";
import { getCustomIcon } from "../../type/utils";
import AddObjectModal from "./AddObjectModal";
import { useFetcher, useLocation } from "react-router-dom";
import AutoOpenMarker from "./AutoOpenMarker";

interface ObjectsMapProps {
  objects: InfraObject[];
  selectedObjectId: string | null;
  onSelect: (objectId: string | null) => void;
  showEventsBar: () => void;
  isAddingObject: boolean;
}

const ObjectsMap: React.FC<ObjectsMapProps> = ({ objects, onSelect, selectedObjectId, showEventsBar, isAddingObject }) => {
  const location = useLocation();
  const { clickedObjectId }: { clickedObjectId: string | null } = location.state || {};
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [clickedPosition, setClickedPosition] = useState<{ lat: number; lon: number } | null>(null);
  const mapRef = useRef<L.Map | null>(null);

  const handleClose = () => {
    setClickedPosition(null);
    onClose();
  };
  // navigate from other page
  useEffect(() => {
    if (clickedObjectId) {
      onSelect(clickedObjectId);
      showEventsBar();
    }
  }, [clickedObjectId]);

  useEffect(() => {
    if (mapRef.current) {
      L.control.fullscreen({ position: "topright" }).addTo(mapRef.current);
    }
  }, []);

  useEffect(() => {
    if (objects.length > 0) {
      mapRef.current?.fitBounds(
        L.latLngBounds(objects.map((obj) => [obj.latitude, obj.longitude])),
        { padding: [50, 50] }
      );
    }
  }, [objects]);

  const MapClickHandler = () => {
    const map = useMap();
    mapRef.current = map;

    useMapEvent("click", (e) => {
      onSelect(null);
      if (isAddingObject) {
        setClickedPosition({ lat: e.latlng.lat, lon: e.latlng.lng });
        onOpen();
      }
    });

    if (!selectedObjectId) {
      return null;
    }

    useEffect(() => {
      if (selectedObjectId) {
        const selectedObj = objects.find((obj) => obj.id === selectedObjectId);
        if (selectedObj) {
          mapRef.current?.flyTo([selectedObj.latitude, selectedObj.longitude], 16, { duration: 0.75 });
        }
      }
    }, [selectedObjectId]);

    return null;
  };

  return (
    <Box width="100%" height="100%" position="relative">
      <MapContainer
        center={objects.length ? [objects[0].latitude, objects[0].longitude] : [21.028346, 105.834131]}
        zoom={15}
        scrollWheelZoom={true}
        style={{ height: "100%", width: "100%" }}
        gestureHandling={true}
        attributionControl={false}
        zoomControl={false}
        fullscreenControl={true}
      >
        <LayersControl position="topright">
          <LayersControl.BaseLayer name="Default">
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
          </LayersControl.BaseLayer>
          <LayersControl.BaseLayer name="Satellite">
            <TileLayer attribution="Google Maps" url="https://mt1.google.com/vt/lyrs=s&x={x}&y={y}&z={z}" />
          </LayersControl.BaseLayer>
          <LayersControl.BaseLayer checked name="Topographic">
            <TileLayer attribution="Google Maps" url="https://mt1.google.com/vt/lyrs=p&x={x}&y={y}&z={z}" />
          </LayersControl.BaseLayer>
        </LayersControl>

        <MapClickHandler />
{/* 
        {objects.map((obj) => (
          <Marker
            key={obj.id}
            position={[obj.latitude, obj.longitude]}
            icon={getCustomIcon(obj.category)}
            eventHandlers={{
              click: () => {
                showEventsBar();
                onSelect(obj.id);
              },
            }}
          >
            <ObjectPopup object={obj} isSelected={selectedObjectId === obj.id} />
          </Marker>
        ))} */}

        {objects.map((obj) => (
          <AutoOpenMarker
            key={obj.id}
            object={obj}
            isSelected={selectedObjectId === obj.id}
            onClick={() => {
              showEventsBar();
              onSelect(obj.id);
            }}
          />
        ))}

      </MapContainer>

      {clickedPosition && (
        <AddObjectModal
          onSelect={onSelect}
          isOpen={isOpen}
          onClose={handleClose}
          latitude={clickedPosition.lat}
          longitude={clickedPosition.lon}
        />
      )}
    </Box>
  );
};

export default ObjectsMap;