package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class VideoSourceConfiguration {

    @XmlAttribute(name = "token")
    private String token;

    @XmlElement(name = "Name", namespace = "http://www.onvif.org/ver10/schema")
    private String name;

    @XmlElement(name = "UseCount", namespace = "http://www.onvif.org/ver10/schema")
    private int useCount;

    @XmlElement(name = "SourceToken", namespace = "http://www.onvif.org/ver10/schema")
    private String sourceToken;

    @XmlElement(name = "Bounds", namespace = "http://www.onvif.org/ver10/schema")
    private Bounds bounds;
}
