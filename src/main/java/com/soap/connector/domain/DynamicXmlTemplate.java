package com.soap.connector.domain;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamicXmlTemplate {

    private String requestTemplate;
    private Map<String, Object> xmlTemplate;
}
