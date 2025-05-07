package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Profile {

    @XmlAttribute(name = "fixed")
    private boolean fixed;

    @XmlAttribute(name = "token")
    private String token;

    @XmlElement(name = "Name", namespace = "http://www.onvif.org/ver10/schema")
    private String name;

    @XmlElement(name = "VideoSourceConfiguration", namespace = "http://www.onvif.org/ver10/schema")
    private VideoSourceConfiguration videoSourceConfiguration;

    @XmlElement(name = "VideoEncoderConfiguration", namespace = "http://www.onvif.org/ver10/schema")
    private VideoEncoderConfiguration videoEncoderConfiguration;
}
