package com.example.camera_service.dto.addOn;

import java.util.List;

import jakarta.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlRootElement(name = "GetProfilesResponse", namespace = "http://www.onvif.org/ver10/media/wsdl")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProfilesResponse {

    @XmlElement(name = "Profiles", namespace = "http://www.onvif.org/ver10/media/wsdl")
    private List<Profile> profiles;
}
