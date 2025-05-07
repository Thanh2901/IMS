package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class VideoEncoderConfiguration {

    @XmlAttribute(name = "token")
    private String token;

    @XmlElement(name = "Name", namespace = "http://www.onvif.org/ver10/schema")
    private String name;

    @XmlElement(name = "UseCount", namespace = "http://www.onvif.org/ver10/schema")
    private int useCount;

    @XmlElement(name = "Encoding", namespace = "http://www.onvif.org/ver10/schema")
    private String encoding;

    @XmlElement(name = "Resolution", namespace = "http://www.onvif.org/ver10/schema")
    private Resolution resolution;

    @XmlElement(name = "Quality", namespace = "http://www.onvif.org/ver10/schema")
    private int quality;

    @XmlElement(name = "RateControl", namespace = "http://www.onvif.org/ver10/schema")
    private RateControl rateControl;

    @XmlElement(name = "Multicast", namespace = "http://www.onvif.org/ver10/schema")
    private Multicast multicast;

    @XmlElement(name = "SessionTimeout", namespace = "http://www.onvif.org/ver10/schema")
    private String sessionTimeout;
}
