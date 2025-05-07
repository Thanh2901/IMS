import React, { useEffect, useRef } from "react";
import { Marker, useMap } from "react-leaflet";
import L from "leaflet";
import { InfraObject } from "../../type/models";
import ObjectPopup from "./ObjectPopup";
import { getCustomIcon } from "../../type/utils";

interface AutoOpenMarkerProps {
  object: InfraObject;
  isSelected: boolean;
  onClick: () => void;
}

const AutoOpenMarker: React.FC<AutoOpenMarkerProps> = ({ object, isSelected, onClick }) => {
  const markerRef = useRef<L.Marker | null>(null);
  const map = useMap();

  useEffect(() => {
    if (isSelected && markerRef.current) {
      // Center the map on the marker with animation
      map.flyTo([object.latitude, object.longitude], 16, { duration: 0.75 });
      
      // Open the popup
      setTimeout(() => {
        if (markerRef.current) {
          markerRef.current.openPopup();
        }
      }, 800); // Wait for flyTo animation to complete
    }
  }, [isSelected, object.latitude, object.longitude, map]);

  return (
    <Marker
      position={[object.latitude, object.longitude]}
      icon={getCustomIcon(object.category)}
      eventHandlers={{
        click: onClick,
      }}
      ref={markerRef}
    >
      <ObjectPopup object={object} isSelected={isSelected} />
    </Marker>
  );
};

export default AutoOpenMarker;