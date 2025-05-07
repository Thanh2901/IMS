package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class MediaUri {
    @XmlElement(name = "Uri", namespace = "http://www.onvif.org/ver10/schema")
    private String source;

    @XmlElement(name = "InvalidAfterConnect", namespace = "http://www.onvif.org/ver10/schema")
    private boolean invalidAfterConnect;

    @XmlElement(name = "InvalidAfterReboot", namespace = "http://www.onvif.org/ver10/schema")
    private boolean invalidAfterReboot;

    @XmlElement(name = "Timeout", namespace = "http://www.onvif.org/ver10/schema")
    private String timeout;
}
