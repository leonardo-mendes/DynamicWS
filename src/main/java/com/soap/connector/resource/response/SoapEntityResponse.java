package com.soap.connector.resource.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SoapEntityResponse {

    private String fieldRequest;
    private String value;
}
