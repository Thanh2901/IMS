import "leaflet"
import { MapOptions } from "leaflet"

declare module "leaflet" {
  namespace control {
    function fullscreen(options?: { position?: string }): Control;
  }
}

declare module "react-leaflet" {
  interface MapContainerProps extends MapOptions {
    gestureHandling?: boolean;
    fullscreenControl?: boolean;
  }
}