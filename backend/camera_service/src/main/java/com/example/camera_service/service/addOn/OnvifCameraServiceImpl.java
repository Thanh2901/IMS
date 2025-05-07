// package com.example.camera_service.service.impl;
//
// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.StringReader;
// import java.io.StringWriter;
// import java.net.HttpURLConnection;
// import java.net.URI;
// import java.net.URISyntaxException;
// import java.net.URL;
// import java.util.List;
// import java.util.regex.Pattern;
// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.transform.OutputKeys;
// import javax.xml.transform.Transformer;
// import javax.xml.transform.TransformerFactory;
// import javax.xml.transform.dom.DOMSource;
// import javax.xml.transform.stream.StreamResult;
//
// import jakarta.xml.bind.JAXBContext;
// import jakarta.xml.bind.Unmarshaller;
// import jakarta.xml.soap.*;
//
// import org.springframework.stereotype.Service;
// import org.w3c.dom.Document;
// import org.w3c.dom.NodeList;
// import org.xml.sax.InputSource;
//
// import com.example.camera_service.dto.*;
// import com.example.camera_service.entity.Camera;
// import com.example.camera_service.service.OnvifCameraService;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Service
// @Slf4j
// public class OnvifCameraServiceImpl implements OnvifCameraService {
//
//    private String deviceServiceUrl;
//    private String mediaServiceUrl;
//    private String username;
//    private String password;
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
//    public GetDeviceInformationResponse getDeviceInformation(Camera camera) throws Exception {
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
//            String xml = sendSOAPRequest(deviceServiceUrl, soapMessage);
//
//            return processDeviceInfo(xml);
//
//        } catch (Exception e) {
//            log.error("Error creating SOAP message for device information", e);
//            throw new RuntimeException("Failed to create SOAP message: " + e.getMessage(), e);
//        }
//    }
//
//    public MediaUri getStreamUri(Camera camera) throws Exception {
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
//            String xml = sendSOAPRequest(mediaServiceUrl, soapMessage);
//
//            return processStreamUri(xml);
//
//        } catch (Exception e) {
//            log.error("Error getting stream URI: ", e);
//            throw new RuntimeException("Failed to get stream URI", e);
//        }
//    }
//
//    public String getScope() {
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
//            SOAPElement getScopeElem = body.addChildElement("GetScopes", "tds");
//
//            // Save changes and prepare message
//            soapMessage.saveChanges();
//            String xml = sendSOAPRequest(mediaServiceUrl, soapMessage);
//            System.out.println(processScopes(xml));
//            // Send SOAP Message
//            return sendSOAPRequest(mediaServiceUrl, soapMessage);
//
//        } catch (Exception e) {
//            log.error("Error creating SOAP message for stream URL", e);
//            throw new RuntimeException("Failed to create SOAP message: " + e.getMessage(), e);
//        }
//    }
//
//    public Profile getProfile(Camera camera) {
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
//            String xml = sendSOAPRequest(deviceServiceUrl, soapMessage);
//
//            return processProfiles(xml);
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
//    public String sendSOAPRequest(String endpoint, SOAPMessage soapMessage) {
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
//    public static String replaceLocalIpWithPublic(String rtspUrl, String ipPublic) throws URISyntaxException {
//        // Phân tích URL RTSP
//        URI parsedUrl = new URI(rtspUrl);
//
//        // Lấy các thành phần của URL
//        String scheme = parsedUrl.getScheme(); // Giao thức (vd: rtsp)
//        String path = parsedUrl.getPath(); // Đường dẫn (vd: /stream)
//        String query = parsedUrl.getQuery(); // Tham số truy vấn (nếu có)
//        String fragment = parsedUrl.getFragment(); // Phân đoạn (nếu có)
//        int port = parsedUrl.getPort(); // Số cổng (vd: 554)
//
//        // Tạo địa chỉ mạng mới với IP công khai và cổng
//        String updatedNetloc = ipPublic + (port != -1 ? ":" + port : "");
//
//        // Xây dựng lại URL
//        URI updatedUrl = new URI(scheme, updatedNetloc, path, query, fragment);
//
//        // Trả về URL đã cập nhật dưới dạng chuỗi
//        return updatedUrl.toString();
//    }
//    // Debugging method to print out SOAP message
//    public String soapMessageToString(SOAPMessage soapMessage) throws Exception {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        soapMessage.writeTo(out);
//        return out.toString();
//    }
//
//    public String processScopes(String xml) {
//        try {
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            dbf.setNamespaceAware(true);
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            Document doc = db.parse(new InputSource(new StringReader(xml)));
//            NodeList responseNodes =
//                    doc.getElementsByTagNameNS("http://www.onvif.org/ver10/device/wsdl", "GetScopesResponse");
//
//            if (responseNodes.getLength() > 0) {
//                TransformerFactory tf = TransformerFactory.newInstance();
//                Transformer transformer = tf.newTransformer();
//                StringWriter writer = new StringWriter();
//                transformer.transform(new DOMSource(responseNodes.item(0)), new StreamResult(writer));
//
//                JAXBContext context = JAXBContext.newInstance(ArrayOfScopes.class);
//                Unmarshaller unmarshaller = context.createUnmarshaller();
//                ArrayOfScopes arrayOfScopes =
//                        (ArrayOfScopes) unmarshaller.unmarshal(new StringReader(writer.toString()));
//
//                if (arrayOfScopes != null && arrayOfScopes.getScopes() != null) {
//                    for (Scope scope : arrayOfScopes.getScopes()) {
//                        log.info("ScopeDef: {}", scope.getScopeDef());
//                        log.info("ScopeItem: {}", scope.getScopeItem());
//                    }
//
//                    Location location = extractCoordinatesFromScopes(arrayOfScopes.getScopes());
//                    if (location != null) {
//                        log.info("Location: {}", location.toString());
//                        return "Successfully processed scopes and location: " + location.toString();
//                    }
//                    return "Successfully processed scopes but no location found";
//                }
//            }
//            return "No scopes found";
//        } catch (Exception e) {
//            log.error("Error processing SOAP response", e);
//            throw new RuntimeException("Failed to process SOAP response: " + e.getMessage(), e);
//        }
//    }
//
//    // Hàm chuyen đổi tọa độ từ dạng DMS (Degrees, Minutes, Seconds) sang Decimal Degrees
//    public double convertToDecimalDegrees(String dms) {
//        // Loại bỏ các ký tự không cần thiết: '°', "'", "''"
//        dms = dms.replace("°", " ").replace("'", " ").replace("''", " ").trim();
//
//        // Tách các phần độ, phút, giây và hướng
//        String[] parts = dms.split(" ");
//
//        // Các phần của tọa độ
//        double degrees = Double.parseDouble(parts[0]);
//        double minutes = Double.parseDouble(parts[1]);
//        double seconds = Double.parseDouble(parts[2]);
//        String direction = parts[3]; // N, S, E, W
//
//        // Tính toán độ thập phân
//        double decimalDegrees = degrees + (minutes / 60) + (seconds / 3600);
//
//        // Nếu là Nam (S) hoặc Tây (W), giá trị là âm
//        if (direction.equals("S") || direction.equals("W")) {
//            decimalDegrees = -decimalDegrees;
//        }
//
//        return decimalDegrees;
//    }
//
//    public Location extractCoordinatesFromScopes(List<Scope> scopes) {
//        // Log all scopes to see the actual format
//        log.info("=== Begin Scope Contents ===");
//        for (Scope scope : scopes) {
//            log.info("Scope Definition: {}", scope.getScopeDef());
//            log.info("Scope Item: {}", scope.getScopeItem());
//            log.info("---");
//        }
//        log.info("=== End Scope Contents ===");
//
//        String regex = "([0-9]+[°Â°][0-9]+'[0-9]+''[NESW]),\\s*([0-9]+[°Â°][0-9]+'[0-9]+''[NESW])";
//        Pattern pattern = Pattern.compile(regex);
//
//        for (Scope scope : scopes) {
//            if (scope.getScopeItem() != null) {
//                log.info("Checking scope item: {}", scope.getScopeItem());
//
//                // Check if it contains any location-related strings
//                if (scope.getScopeItem().toLowerCase().contains("location")
//                        || scope.getScopeItem().toLowerCase().contains("geo")
//                        || scope.getScopeItem().toLowerCase().contains("lat")
//                        || scope.getScopeItem().toLowerCase().contains("long")) {
//
//                    log.info("Found potential location scope: {}", scope.getScopeItem());
//                }
//            }
//        }
//        return null;
//    }
//
//    public MediaUri processStreamUri(String xml) {
//        try {
//            // Create document builder
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            dbf.setNamespaceAware(true);
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            // Parse XML
//            Document doc = db.parse(new InputSource(new StringReader(xml)));
//
//            // Get GetStreamUriResponse element
//            NodeList responseNodes =
//                    doc.getElementsByTagNameNS("http://www.onvif.org/ver10/media/wsdl", "GetStreamUriResponse");
//
//            if (responseNodes.getLength() > 0) {
//                // Convert to string for JAXB
//                TransformerFactory tf = TransformerFactory.newInstance();
//                Transformer transformer = tf.newTransformer();
//                StringWriter writer = new StringWriter();
//                transformer.transform(new DOMSource(responseNodes.item(0)), new StreamResult(writer));
//
//                // Unmarshal
//                JAXBContext context = JAXBContext.newInstance(GetStreamUriResponse.class);
//                Unmarshaller unmarshaller = context.createUnmarshaller();
//                GetStreamUriResponse response =
//                        (GetStreamUriResponse) unmarshaller.unmarshal(new StringReader(writer.toString()));
//
//                return response.getMediaUri();
//            }
//            return null;
//        } catch (Exception e) {
//            log.error("Error processing SOAP response", e);
//            throw new RuntimeException("Failed to process SOAP response: " + e.getMessage(), e);
//        }
//    }
//
//    public GetDeviceInformationResponse processDeviceInfo(String xml) {
//        try {
//            // Create document builder
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            dbf.setNamespaceAware(true);
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            // Parse XML
//            Document doc = db.parse(new InputSource(new StringReader(xml)));
//
//            // Get GetDeviceInformationResponse element
//            NodeList responseNodes = doc.getElementsByTagNameNS(
//                    "http://www.onvif.org/ver10/device/wsdl", "GetDeviceInformationResponse");
//
//            if (responseNodes.getLength() > 0) {
//                // Convert to string for JAXB
//                TransformerFactory tf = TransformerFactory.newInstance();
//                Transformer transformer = tf.newTransformer();
//                StringWriter writer = new StringWriter();
//                transformer.transform(new DOMSource(responseNodes.item(0)), new StreamResult(writer));
//
//                // Unmarshal
//                JAXBContext context = JAXBContext.newInstance(GetDeviceInformationResponse.class);
//                Unmarshaller unmarshaller = context.createUnmarshaller();
//                GetDeviceInformationResponse response =
//                        (GetDeviceInformationResponse) unmarshaller.unmarshal(new StringReader(writer.toString()));
//
//                // Print device information
//                //                if (response != null) {
//                //                    log.info("Manufacturer: {}", response.getManufacturer());
//                //                    log.info("Model: {}", response.getModel());
//                //                    log.info("Firmware Version: {}", response.getFirmwareVersion());
//                //                    log.info("Serial Number: {}", response.getSerialNumber());
//                //                    log.info("Hardware ID: {}", response.getHardwareId());
//                //                    return "Successfully processed device information";
//                //                }
//                return response;
//            }
//            return null;
//        } catch (Exception e) {
//            log.error("Error processing SOAP response", e);
//            throw new RuntimeException("Failed to process SOAP response: " + e.getMessage(), e);
//        }
//    }
//
//    public Profile processProfiles(String xml) {
//        try {
//            // Create document builder
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            dbf.setNamespaceAware(true);
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            // Parse XML
//            Document doc = db.parse(new InputSource(new StringReader(xml)));
//
//            // Get GetProfilesResponse element
//            NodeList responseNodes =
//                    doc.getElementsByTagNameNS("http://www.onvif.org/ver10/media/wsdl", "GetProfilesResponse");
//
//            if (responseNodes.getLength() > 0) {
//                // Convert to string for JAXB
//                TransformerFactory tf = TransformerFactory.newInstance();
//                Transformer transformer = tf.newTransformer();
//                StringWriter writer = new StringWriter();
//                transformer.transform(new DOMSource(responseNodes.item(0)), new StreamResult(writer));
//
//                // Unmarshal
//                JAXBContext context = JAXBContext.newInstance(GetProfilesResponse.class);
//                Unmarshaller unmarshaller = context.createUnmarshaller();
//                GetProfilesResponse response =
//                        (GetProfilesResponse) unmarshaller.unmarshal(new StringReader(writer.toString()));
//
//                // Process and log profile information
//                //                if (response != null && response.getProfiles() != null) {
//                //                    for (Profile profile : response.getProfiles()) {
//                //                        log.info("Profile Token: {}", profile.getToken());
//                //                        log.info("Profile Name: {}", profile.getName());
//                //
//                //                        if (profile.getVideoSourceConfiguration() != null) {
//                //                            VideoSourceConfiguration vsc = profile.getVideoSourceConfiguration();
//                //                            log.info("Video Source Name: {}", vsc.getName());
//                //                            log.info("Source Token: {}", vsc.getSourceToken());
//                //
//                //                            if (vsc.getBounds() != null) {
//                //                                Bounds bounds = vsc.getBounds();
//                //                                log.info("Bounds: {}x{} at ({},{})",
//                //                                        bounds.getWidth(), bounds.getHeight(),
//                //                                        bounds.getX(), bounds.getY());
//                //                            }
//                //                        }
//                //
//                //                        if (profile.getVideoEncoderConfiguration() != null) {
//                //                            VideoEncoderConfiguration vec = profile.getVideoEncoderConfiguration();
//                //                            log.info("Encoder Name: {}", vec.getName());
//                //                            log.info("Encoding: {}", vec.getEncoding());
//                //
//                //                            if (vec.getResolution() != null) {
//                //                                Resolution res = vec.getResolution();
//                //                                log.info("Resolution: {}x{}", res.getWidth(), res.getHeight());
//                //                            }
//                //
//                //                            if (vec.getRateControl() != null) {
//                //                                RateControl rc = vec.getRateControl();
//                //                                log.info("Frame Rate Limit: {}", rc.getFrameRateLimit());
//                //                                log.info("Bitrate Limit: {}", rc.getBitrateLimit());
//                //                            }
//                //
//                //                            if (vec.getMulticast() != null && vec.getMulticast().getAddress() !=
// null)
//                // {
//                //                                Address addr = vec.getMulticast().getAddress();
//                //                                log.info("Multicast Address: {}", addr.getIpv4Address());
//                //                                log.info("Multicast Port: {}", vec.getMulticast().getPort());
//                //                            }
//                //                        }
//                //                        log.info("----------------------------------------");
//                //                    }
//                //                    return "Successfully processed profiles";
//                //                }
//                if (response != null && response.getProfiles() != null) {
//                    for (Profile profile : response.getProfiles()) {
//                        return profile;
//                    }
//                }
//            }
//            return null;
//        } catch (Exception e) {
//            log.error("Error processing SOAP response", e);
//            throw new RuntimeException("Failed to process SOAP response: " + e.getMessage(), e);
//        }
//    }
// }
