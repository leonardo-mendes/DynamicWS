package com.soap.connector.service;

import com.soap.connector.resource.request.SoapConnectionRequest;
import com.soap.connector.resource.response.SoapEntityResponse;
import java.util.List;
import java.util.Map;

public interface DynamicSoapService {

    List<Map<String, List<SoapEntityResponse>>> executeDynamicSoap(
            final SoapConnectionRequest request);
}
