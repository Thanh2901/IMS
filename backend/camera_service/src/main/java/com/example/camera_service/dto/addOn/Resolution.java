package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Resolution {

    @XmlElement(name = "Width", namespace = "http://www.onvif.org/ver10/schema")
    private int width;

    @XmlElement(name = "Height", namespace = "http://www.onvif.org/ver10/schema")
    private int height;
}
