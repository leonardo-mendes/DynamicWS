package com.soap.connector.resource;

import static org.springframework.http.HttpStatus.OK;

import com.soap.connector.resource.request.SoapConnectionRequest;
import com.soap.connector.resource.response.SoapEntityResponse;
import com.soap.connector.service.DynamicSoapService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/soap-operation")
@RequiredArgsConstructor
public class SoapConnectorResource {

    private final DynamicSoapService dynamicSoapService;

    @PostMapping
    @ResponseStatus(code = OK)
    public List<Map<String, List<SoapEntityResponse>>> callExternalConnection(
            @RequestBody SoapConnectionRequest request) {
        log.info(request.toString());
        return dynamicSoapService.executeDynamicSoap(request);
    }
}
