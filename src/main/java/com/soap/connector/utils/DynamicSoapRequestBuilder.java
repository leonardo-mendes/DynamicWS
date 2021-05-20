package com.soap.connector.utils;

import com.soap.connector.domain.DynamicParameter;
import com.soap.connector.domain.DynamicXmlTemplate;
import com.soap.connector.resource.request.SoapFieldConnectionRequest;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@UtilityClass
public class DynamicSoapRequestBuilder {

    public SOAPMessage buildRequest(
            String soapAction,
            DynamicXmlTemplate xmlTemplate,
            List<SoapFieldConnectionRequest> requestValues) {
        Map<String, String> fieldRequestMap =
                requestValues
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        SoapFieldConnectionRequest::getName,
                                        SoapFieldConnectionRequest::getValue));
        Map<String, DynamicParameter> mapValues = new HashMap<>();
        fillMapValues(xmlTemplate.getXmlTemplate(), mapValues, fieldRequestMap);
        return createSOAPRequest(
                soapAction, retrieveRequestTemplate(mapValues, xmlTemplate.getRequestTemplate()));
    }

    private void fillMapValues(
            Map<String, Object> xmlTemplate,
            Map<String, DynamicParameter> mapValues,
            Map<String, String> fieldRequestMap) {

        xmlTemplate
                .keySet()
                .forEach(
                        key -> {
                            Object value = xmlTemplate.get(key);
                            if (value instanceof Map) {
                                fillMapValues(
                                        (Map<String, Object>) value, mapValues, fieldRequestMap);
                            }
                            if (value instanceof String) {
                                String parameterValue = (String) value;
                                if (parameterValue.contains("?")) {
                                    String[] sanitizedKey = key.split(".\\:");
                                    String requestValue = fieldRequestMap.get(sanitizedKey[1]);
                                    mapValues.put(
                                            key,
                                            DynamicParameter.builder()
                                                    .xmlParam(parameterValue)
                                                    .requestParam(requestValue)
                                                    .build());
                                }
                            }
                        });
    }

    private String retrieveRequestTemplate(Map<String, DynamicParameter> parameters, String xml) {
        for (Map.Entry<String, DynamicParameter> entry : parameters.entrySet()) {
            String oldString =
                    String.format("%s>%s", entry.getKey(), entry.getValue().getXmlParam());
            String newString =
                    String.format("%s>%s", entry.getKey(), entry.getValue().getRequestParam());
            xml = xml.replace(oldString, newString);
        }
        return xml;
    }

    private SOAPMessage createSOAPRequest(String soapAction, String requestTemplate) {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();

            createSoapEnvelope(soapMessage, requestTemplate);

            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", soapAction);

            soapMessage.saveChanges();

            return soapMessage;
        } catch (SOAPException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createSoapEnvelope(SOAPMessage soapMessage, String requestTemplate) {
        try {
            SOAPPart soapPart = soapMessage.getSOAPPart();

            String myNamespace = "ns1";
            String myNamespaceURI = "http://tempuri.org/";

            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

            // SOAP Body
            SOAPBody soapBody = envelope.getBody();
            Document document = convertStringToDocument(requestTemplate);
            soapBody.addDocument(document);
        } catch (SOAPException soapException) {
            throw new RuntimeException(soapException.getMessage());
        }
    }

    private Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
