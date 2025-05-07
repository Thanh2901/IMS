// package com.example.camera_service.service.impl;
//
// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.StringWriter;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import javax.xml.transform.OutputKeys;
// import javax.xml.transform.Transformer;
// import javax.xml.transform.TransformerFactory;
// import javax.xml.transform.dom.DOMSource;
// import javax.xml.transform.stream.StreamResult;
//
// import jakarta.xml.soap.*;
//
// import org.springframework.stereotype.Service;
//
// import com.example.camera_service.entity.Camera;
// import com.example.camera_service.service.OnvifSOAPMessageService;
//
// import lombok.AccessLevel;
// import lombok.RequiredArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import lombok.extern.slf4j.Slf4j;
//
// @Service
// @RequiredArgsConstructor
// @Slf4j
// @FieldDefaults(level = AccessLevel.PRIVATE)
// public class OnvifSOAPMessageServiceImpl implements OnvifSOAPMessageService {
//    final SoapXmlParserServiceImpl soapXmlParserService;
//    String deviceServiceUrl;
//    String mediaServiceUrl;
//    String username;
//    String password;
//
//    public void cameraConnection(Camera camera) {
//        this.deviceServiceUrl =
//                String.format("http://%s:%d/onvif/device_service", camera.getIpAddress(), camera.getImagingPort());
//        this.mediaServiceUrl =
//                String.format("http://%s:%d/onvif/media_service", camera.getIpAddress(), camera.getImagingPort());
//        this.username = camera.getUsername();
//        this.password = camera.getPassword();
//    }
//
//    public String getDeviceInformation(Camera camera) throws Exception {
//        try {
//            // Create SOAP Message Factory
//            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
//            SOAPMessage soapMessage = messageFactory.createMessage();
//
//            // Create SOAP Envelope
//            SOAPPart soapPart = soapMessage.getSOAPPart();
//            SOAPEnvelope envelope = soapPart.getEnvelope();
//            envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
//            envelope.addNamespaceDeclaration("tds", "http://www.onvif.org/ver10/device/wsdl");
//            envelope.addNamespaceDeclaration(
//                    "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
//
//            // SOAP Header with WS-Security
//            SOAPHeader header = envelope.getHeader();
//            SOAPElement securityElem = header.addChildElement("Security", "wsse");
//            SOAPElement usernameTokenElem = securityElem.addChildElement("UsernameToken", "wsse");
//            SOAPElement usernameElem = usernameTokenElem.addChildElement("Username", "wsse");
//            usernameElem.addTextNode(username);
//            SOAPElement passwordElem = usernameTokenElem.addChildElement("Password", "wsse");
//            passwordElem.addTextNode(password);
//
//            // SOAP Body
//            SOAPBody body = envelope.getBody();
//            SOAPElement getDeviceInfoElem = body.addChildElement("GetDeviceInformation", "tds");
//
//            // Save changes and prepare message
//            soapMessage.saveChanges();
//
//            // Send SOAP Message
//            return sendSOAPRequest(deviceServiceUrl, soapMessage);
//
//        } catch (Exception e) {
//            log.error("Error creating SOAP message for device information", e);
//            throw new RuntimeException("Failed to create SOAP message: " + e.getMessage(), e);
//        }
//    }
//
//    public String getStreamUri(Camera camera) throws Exception {
//        try {
//            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
//            SOAPMessage soapMessage = messageFactory.createMessage();
//
//            SOAPPart soapPart = soapMessage.getSOAPPart();
//            SOAPEnvelope envelope = soapPart.getEnvelope();
//            envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
//            envelope.addNamespaceDeclaration("trt", "http://www.onvif.org/ver10/media/wsdl");
//            envelope.addNamespaceDeclaration("tt", "http://www.onvif.org/ver10/schema");
//            envelope.addNamespaceDeclaration(
//                    "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
//
//            // Add security header
//            SOAPHeader header = envelope.getHeader();
//            SOAPElement securityElem = header.addChildElement("Security", "wsse");
//            SOAPElement usernameTokenElem = securityElem.addChildElement("UsernameToken", "wsse");
//
//            SOAPElement usernameElem = usernameTokenElem.addChildElement("Username", "wsse");
//            usernameElem.addTextNode(username);
//            System.out.println(username);
//
//            SOAPElement passwordElem = usernameTokenElem.addChildElement("Password", "wsse");
//            passwordElem.addTextNode(password);
//            System.out.println(password);
//
//            // Add body
//            SOAPBody body = envelope.getBody();
//            SOAPElement getStreamUriElem = body.addChildElement("GetStreamUri", "trt");
//
//            SOAPElement streamSetupElem = getStreamUriElem.addChildElement("StreamSetup", "trt");
//            SOAPElement streamElem = streamSetupElem.addChildElement("Stream", "tt");
//            streamElem.addTextNode("RTP-Unicast");
//
//            SOAPElement transportElem = streamSetupElem.addChildElement("Transport", "tt");
//            SOAPElement protocolElem = transportElem.addChildElement("Protocol", "tt");
//            protocolElem.addTextNode("RTSP");
//
//            SOAPElement profileTokenElem = getStreamUriElem.addChildElement("ProfileToken", "trt");
//            profileTokenElem.addTextNode("0");
//
//            soapMessage.saveChanges();
//
//            return sendSOAPRequest(mediaServiceUrl, soapMessage);
//
//        } catch (Exception e) {
//            log.error("Error getting stream URI: ", e);
//            throw new RuntimeException("Failed to get stream URI", e);
//        }
//    }
//
//    //    public String getScope() throws Exception {
//    //        try {
//    //            // Create SOAP Message Factory
//    //            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
//    //            SOAPMessage soapMessage = messageFactory.createMessage();
//    //
//    //            // Create SOAP Envelope
//    //            SOAPPart soapPart = soapMessage.getSOAPPart();
//    //            SOAPEnvelope envelope = soapPart.getEnvelope();
//    //            envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
//    //            envelope.addNamespaceDeclaration("tds", "http://www.onvif.org/ver10/device/wsdl");
//    //            envelope.addNamespaceDeclaration("wsse",
//    // "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
//    //
//    //            // SOAP Header with WS-Security
//    //            SOAPHeader header = envelope.getHeader();
//    //            SOAPElement securityElem = header.addChildElement("Security", "wsse");
//    //            SOAPElement usernameTokenElem = securityElem.addChildElement("UsernameToken", "wsse");
//    //            SOAPElement usernameElem = usernameTokenElem.addChildElement("Username", "wsse");
//    //            usernameElem.addTextNode(username);
//    //            SOAPElement passwordElem = usernameTokenElem.addChildElement("Password", "wsse");
//    //            passwordElem.addTextNode(password);
//    //
//    //            // SOAP Body
//    //            SOAPBody body = envelope.getBody();
//    //            SOAPElement getScopeElem = body.addChildElement("GetScopes", "tds");
//    //
//    //            // Save changes and prepare message
//    //            soapMessage.saveChanges();
//    //            String xml = sendSOAPRequest(mediaServiceUrl, soapMessage);
//    //            System.out.println(processScopes(xml));
//    //            // Send SOAP Message
//    //            return sendSOAPRequest(mediaServiceUrl, soapMessage);
//    //
//    //        } catch (Exception e) {
//    //            log.error("Error creating SOAP message for stream URL", e);
//    //            throw new RuntimeException("Failed to create SOAP message: " + e.getMessage(), e);
//    //        }
//    //    }
//
//    public String getProfile(Camera camera) throws Exception {
//        try {
//            // Create SOAP Message Factory
//            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
//            SOAPMessage soapMessage = messageFactory.createMessage();
//
//            // Create SOAP Envelope
//            SOAPPart soapPart = soapMessage.getSOAPPart();
//            SOAPEnvelope envelope = soapPart.getEnvelope();
//            envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
//            envelope.addNamespaceDeclaration("trt", "http://www.onvif.org/ver10/media/wsdl");
//            envelope.addNamespaceDeclaration(
//                    "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
//
//            // SOAP Header with WS-Security
//            SOAPHeader header = envelope.getHeader();
//            SOAPElement securityElem = header.addChildElement("Security", "wsse");
//            SOAPElement usernameTokenElem = securityElem.addChildElement("UsernameToken", "wsse");
//            SOAPElement usernameElem = usernameTokenElem.addChildElement("Username", "wsse");
//            usernameElem.addTextNode(username);
//            SOAPElement passwordElem = usernameTokenElem.addChildElement("Password", "wsse");
//            passwordElem.addTextNode(password);
//
//            // SOAP Body
//            SOAPBody body = envelope.getBody();
//            SOAPElement getProfileElem = body.addChildElement("GetProfiles", "trt");
//
//            // Save changes and prepare message
//            soapMessage.saveChanges();
//
//            // Send SOAP Message
//            return sendSOAPRequest(deviceServiceUrl, soapMessage);
//
//        } catch (Exception e) {
//            log.error("Error creating SOAP message for stream URI", e);
//            throw new RuntimeException("Failed to create SOAP message: " + e.getMessage(), e);
//        }
//    }
//
//    public static String extractSoapBody(String soapXml) throws Exception {
//        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
//        SOAPMessage message = factory.createMessage(null, new ByteArrayInputStream(soapXml.getBytes()));
//
//        SOAPBody body = message.getSOAPBody();
//
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//
//        StringWriter writer = new StringWriter();
//        transformer.transform(new DOMSource(body), new StreamResult(writer));
//
//        return writer.toString();
//    }
//
//    private String sendSOAPRequest(String endpoint, SOAPMessage soapMessage) throws Exception {
//        try {
//            // Prepare HTTP Connection
//            URL url = new URL(endpoint);
//            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//            httpConn.setRequestMethod("POST");
//            httpConn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
//            httpConn.setDoOutput(true);
//            httpConn.setDoInput(true);
//
//            // Send SOAP Message
//            try (SOAPConnection soapConnection =
//                    SOAPConnectionFactory.newInstance().createConnection()) {
//                SOAPMessage response = soapConnection.call(soapMessage, url);
//
//                // Convert response to formatted string
//                //                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                //                response.writeTo(out);
//                //                String responseString = out.toString();
//                String responseString = soapMessageToString(response);
//
//                log.info("SOAP Response received from {}", endpoint);
//                return extractSoapBody(responseString);
//            }
//        } catch (Exception e) {
//            log.error("Error sending SOAP request to {}", endpoint, e);
//            throw new RuntimeException("SOAP request failed: " + e.getMessage(), e);
//        }
//    }
//
//    private String soapMessageToString(SOAPMessage soapMessage) throws Exception {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        soapMessage.writeTo(out);
//        return out.toString();
//    }
// }
