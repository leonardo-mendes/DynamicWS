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
public class SoapResponseEntityRequest implements Serializable {

    private String entityName;
    private List<String> responseFields;
}
