package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Multicast {

    @XmlElement(name = "Address", namespace = "http://www.onvif.org/ver10/schema")
    private Address address;

    @XmlElement(name = "Port", namespace = "http://www.onvif.org/ver10/schema")
    private int port;

    @XmlElement(name = "TTL", namespace = "http://www.onvif.org/ver10/schema")
    private int ttl;

    @XmlElement(name = "AutoStart", namespace = "http://www.onvif.org/ver10/schema")
    private boolean autoStart;
}
