package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "GetStreamUriResponse", namespace = "http://www.onvif.org/ver10/media/wsdl")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetStreamUriResponse {

    @XmlElement(name = "MediaUri", namespace = "http://www.onvif.org/ver10/media/wsdl")
    private MediaUri mediaUri;
}
