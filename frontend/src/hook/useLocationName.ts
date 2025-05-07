import { useState } from "react";

const useLocationName = (lat: number, lon: number) => {
  const [locationName, setLocationName] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isError, setIsError] = useState<boolean>(false);

  const fetchLocationName = async () => {
    setIsLoading(true);
    try {
      const res = await fetch(`https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lon}&format=json`);
      console.log(`https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lon}&format=json`);
      const data = await res.json();

      if (data.error) {
        throw new Error(data.error);
      }

      console.log("Fetching object location: ", data);
      setLocationName(data.display_name);

    } catch (error) {
      setIsError(true);
      console.log(error);

    } finally {
      setIsLoading(false);
    }
  };


  return { fetchLocationName, locationName, isLoading, isError };
};

export default useLocationName;