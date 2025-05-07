package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RateControl {

    @XmlElement(name = "FrameRateLimit", namespace = "http://www.onvif.org/ver10/schema")
    private int frameRateLimit;

    @XmlElement(name = "EncodingInterval", namespace = "http://www.onvif.org/ver10/schema")
    private int encodingInterval;

    @XmlElement(name = "BitrateLimit", namespace = "http://www.onvif.org/ver10/schema")
    private int bitrateLimit;
}
