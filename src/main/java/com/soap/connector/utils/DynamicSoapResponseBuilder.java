package com.soap.connector.utils;

import static javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD;
import static javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET;

import com.soap.connector.resource.request.SoapResponseEntityRequest;
import com.soap.connector.resource.response.SoapEntityResponse;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.experimental.UtilityClass;
import org.json.XML;
import org.w3c.dom.Document;

@UtilityClass
public class DynamicSoapResponseBuilder {

    public List<Map<String, List<SoapEntityResponse>>> getSoapResponse(
            Document document, List<SoapResponseEntityRequest> responseEntitiesRequest) {

        List<Map<String, List<SoapEntityResponse>>> response = new LinkedList<>();
        fillResponseMapValues(
                XML.toJSONObject(convertDocumentToXML(document)).toMap(),
                responseEntitiesRequest
                        .parallelStream()
                        .collect(
                                Collectors.toMap(
                                        SoapResponseEntityRequest::getEntityName,
                                        SoapResponseEntityRequest::getResponseFields)),
                response,
                "");
        return response;
    }

    private String convertDocumentToXML(Document document) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute(ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer trans = tf.newTransformer();
            StringWriter sw = new StringWriter();
            trans.transform(new DOMSource(document), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void fillResponseMapValues(
            Map<String, Object> convertedXML,
            Map<String, List<String>> responseAttributes,
            List<Map<String, List<SoapEntityResponse>>> response,
            String entity) {

        Map<String, List<SoapEntityResponse>> responseMap = new HashMap<>();
        convertedXML
                .keySet()
                .forEach(
                        param -> {
                            Object value = convertedXML.get(param);
                            if (value instanceof Map) {
                                fillResponseMapValues(
                                        (Map<String, Object>) value,
                                        responseAttributes,
                                        response,
                                        param);
                            } else if (value instanceof List) {
                                for (Map<String, Object> valueMap :
                                        (List<Map<String, Object>>) value) {
                                    fillResponseMapValues(
                                            valueMap, responseAttributes, response, param);
                                }
                            } else {
                                addSoapEntityResponseToMap(
                                        param, entity, responseAttributes, responseMap, value);
                            }
                        });
        addResponseMapToResponse(responseMap, response);
    }

    private void addSoapEntityResponseToMap(
            String param,
            String entity,
            Map<String, List<String>> responseAttributes,
            Map<String, List<SoapEntityResponse>> responseMap,
            Object value) {
        String[] splitParam = param.split(".\\:");
        if (responseAttributes.containsKey(entity)
                && responseAttributes
                        .get(entity)
                        .contains(splitParam.length > 1 ? splitParam[1] : splitParam[0])) {
            if (responseMap.containsKey(entity)) {
                responseMap.get(entity).add(buildSoapEntityResponse(param, value));
            } else {
                responseMap.put(entity, createSoapEntities(param, value));
            }
        }
    }

    private SoapEntityResponse buildSoapEntityResponse(String param, Object value) {
        return SoapEntityResponse.builder().fieldRequest(param).value(value.toString()).build();
    }

    private List<SoapEntityResponse> createSoapEntities(String param, Object value) {
        return Arrays.asList(buildSoapEntityResponse(param, value));
    }

    private void addResponseMapToResponse(
            Map<String, List<SoapEntityResponse>> responseMap,
            List<Map<String, List<SoapEntityResponse>>> response) {
        if (!responseMap.isEmpty()) {
            response.add(responseMap);
        }
    }
}
