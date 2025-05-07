package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "GetDeviceInformationResponse", namespace = "http://www.onvif.org/ver10/device/wsdl")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDeviceInformationResponse {
    @XmlElement(name = "Manufacturer", namespace = "http://www.onvif.org/ver10/device/wsdl")
    private String manufacturer;

    @XmlElement(name = "Model", namespace = "http://www.onvif.org/ver10/device/wsdl")
    private String model;

    @XmlElement(name = "FirmwareVersion", namespace = "http://www.onvif.org/ver10/device/wsdl")
    private String firmwareVersion;

    @XmlElement(name = "SerialNumber", namespace = "http://www.onvif.org/ver10/device/wsdl")
    private String serialNumber;

    @XmlElement(name = "HardwareId", namespace = "http://www.onvif.org/ver10/device/wsdl")
    private String hardwareId;
}
