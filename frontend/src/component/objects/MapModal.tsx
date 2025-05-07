import { Box, Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, ModalCloseButton, useDisclosure } from "@chakra-ui/react";
import { MapContainer, Marker, TileLayer, LayersControl, useMap, useMapEvent } from "react-leaflet";
import { useRef, useState } from "react";

interface MapModalProps {
  isOpen: boolean;
  onClose: () => void;
  setLatitude: (lat: number) => void;
  setLongitude: (lon: number) => void;
};

const MapModal: React.FC<MapModalProps> = ({ isOpen, onClose, setLatitude, setLongitude }) => {
  const mapRef = useRef<L.Map | null>(null);

  const MapClickHandler = () => {
    const map = useMap();
    mapRef.current = map;

    useMapEvent("click", (e) => {
      setLatitude(e.latlng.lat);
      setLongitude(e.latlng.lng);
      onClose();
    });

    return null;
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="xl">
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Map</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <Box width="100%" height="80vh" position="relative">
            <MapContainer
              center={[21.028346, 105.834131]}
              zoom={15}
              scrollWheelZoom={true}
              style={{ height: "100%", width: "100%" }}
              attributionControl={false}
              zoomControl={false}
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
            </MapContainer>
          </Box>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
};

export default MapModal;