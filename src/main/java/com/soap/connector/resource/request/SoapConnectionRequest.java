package com.soap.connector.resource.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoapConnectionRequest implements Serializable {

    private String wsdlUrl;
    private SoapOperationRequest operation;
}
