package com.soap.connector.domain;

import lombok.Data;

@Data
public class DynamicBinding {

    private String soapEndpoint;
    private String soapAction;
    private String wsdlEndpoint;
    private String namespaceURI;
    private String operation;
    private String bindingName;
    private String portType;
    private String objectRequest;

    public DynamicBinding(String wsdlUrl, String operation, String objectRequest) {
        this.wsdlEndpoint = wsdlUrl;
        this.soapEndpoint = wsdlUrl.split("\\?")[0];
        this.operation = operation;
        this.objectRequest = objectRequest;
    }
}
