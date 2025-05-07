// package com.example.camera_service.service;
//
// import java.util.List;
//
// import jakarta.xml.soap.SOAPMessage;
//
// import com.example.camera_service.dto.*;
// import com.example.camera_service.entity.Camera;
//
// public interface OnvifCameraService {
//    GetDeviceInformationResponse getDeviceInformation(Camera camera) throws Exception;
//
//    MediaUri getStreamUri(Camera camera) throws Exception;
//
//    String getScope() throws Exception;
//
//    Profile getProfile(Camera camera) throws Exception;
//
//    String sendSOAPRequest(String endpoint, SOAPMessage soapMessage);
//
//    String soapMessageToString(SOAPMessage soapMessage) throws Exception;
//
//    String processScopes(String xml);
//
//    Location extractCoordinatesFromScopes(List<Scope> scopes);
//
//    double convertToDecimalDegrees(String dms);
//
//    MediaUri processStreamUri(String xml);
//
//    GetDeviceInformationResponse processDeviceInfo(String xml);
//
//    Profile processProfiles(String xml);
// }
