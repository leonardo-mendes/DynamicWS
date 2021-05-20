package com.soap.connector.service.impl;

import com.soap.connector.resource.request.SoapResponseEntityRequest;
import com.soap.connector.resource.response.SoapEntityResponse;
import com.soap.connector.service.SoapExecutorService;
import com.soap.connector.utils.DynamicSoapResponseBuilder;
import java.util.List;
import java.util.Map;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoapExecutorServiceImpl implements SoapExecutorService {

    @Override
    public List<Map<String, List<SoapEntityResponse>>> executeOperation(
            String soapEndpointUrl,
            SOAPMessage request,
            List<SoapResponseEntityRequest> responseEntitiesRequest) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(request, soapEndpointUrl);
            SOAPBody soapBody = soapResponse.getSOAPBody();

            soapConnection.close();
            return DynamicSoapResponseBuilder.getSoapResponse(
                    soapBody.getOwnerDocument(), responseEntitiesRequest);
        } catch (Exception e) {
            log.error(
                    "\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            throw new RuntimeException(e.getMessage());
        }
    }
}
