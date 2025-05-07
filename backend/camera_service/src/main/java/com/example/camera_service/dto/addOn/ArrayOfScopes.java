package com.example.camera_service.dto.addOn;

import java.util.List;

import jakarta.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlRootElement(name = "GetScopesResponse", namespace = "http://www.onvif.org/ver10/device/wsdl")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArrayOfScopes {

    @XmlElement(name = "Scopes", namespace = "http://www.onvif.org/ver10/device/wsdl", nillable = true)
    private List<Scope> scopes;
}
