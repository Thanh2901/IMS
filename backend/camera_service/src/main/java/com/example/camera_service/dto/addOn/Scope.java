package com.example.camera_service.dto.addOn;

import jakarta.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Scope {

    @XmlElement(name = "ScopeDef", namespace = "http://www.onvif.org/ver10/schema")
    private String scopeDef;

    @XmlElement(name = "ScopeItem", namespace = "http://www.onvif.org/ver10/schema")
    private String scopeItem;
}
