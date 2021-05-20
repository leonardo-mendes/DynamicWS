package com.soap.connector.service;

import com.soap.connector.resource.request.SoapResponseEntityRequest;
import com.soap.connector.resource.response.SoapEntityResponse;
import java.util.List;
import java.util.Map;
import javax.xml.soap.SOAPMessage;

public interface SoapExecutorService {

    List<Map<String, List<SoapEntityResponse>>> executeOperation(
            String soapEndpointUrl,
            SOAPMessage requestTemplate,
            List<SoapResponseEntityRequest> responseEntitiesRequest);
}
