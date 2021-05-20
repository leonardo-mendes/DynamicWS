package com.soap.connector.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamicParameter {

    private String xmlParam;
    private String requestParam;
}
