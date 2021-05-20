package com.soap.connector.resource.request;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoapOperationRequest implements Serializable {

    private String operationName;
    private SoapEntityRequest request;
    private List<SoapResponseEntityRequest> responseEntities;
}
