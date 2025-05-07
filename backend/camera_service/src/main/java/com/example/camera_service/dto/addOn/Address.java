package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Address {

    @XmlElement(name = "Type", namespace = "http://www.onvif.org/ver10/schema")
    private String type;

    @XmlElement(name = "IPv4Address", namespace = "http://www.onvif.org/ver10/schema")
    private String ipv4Address;

    @XmlElement(name = "IPv6Address", namespace = "http://www.onvif.org/ver10/schema")
    private String ipv6Address;
}
