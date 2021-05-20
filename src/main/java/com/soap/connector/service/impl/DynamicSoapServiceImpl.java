package com.soap.connector.service.impl;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import com.soap.connector.domain.DynamicBinding;
import com.soap.connector.resource.request.SoapConnectionRequest;
import com.soap.connector.resource.request.SoapFieldConnectionRequest;
import com.soap.connector.resource.response.SoapEntityResponse;
import com.soap.connector.service.DynamicSoapService;
import com.soap.connector.service.SoapExecutorService;
import com.soap.connector.utils.DynamicSoapRequestBuilder;
import com.soap.connector.utils.XmlTemplateBuilder;
import java.util.List;
import java.util.Map;
import javax.xml.soap.SOAPMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicSoapServiceImpl implements DynamicSoapService {

    private final WSDLParser parser = new WSDLParser();
    private final SoapExecutorService soapExecutorService;

    @Override
    public List<Map<String, List<SoapEntityResponse>>> executeDynamicSoap(
            SoapConnectionRequest request) {
        DynamicBinding dynamicBinding =
                new DynamicBinding(
                        request.getWsdlUrl(),
                        request.getOperation().getOperationName(),
                        request.getOperation().getRequest().getEntityName());

        Definitions wsdl = parser.parse(dynamicBinding.getWsdlEndpoint());
        fillDynamicBinding(dynamicBinding, wsdl);

        return soapExecutorService.executeOperation(
                dynamicBinding.getSoapEndpoint(),
                retrieveSoapRequest(
                        wsdl,
                        dynamicBinding,
                        request.getOperation().getRequest().getRequestFields()),
                request.getOperation().getResponseEntities());
    }

    private SOAPMessage retrieveSoapRequest(
            Definitions wsdl,
            DynamicBinding dynamicBinding,
            List<SoapFieldConnectionRequest> requestFields) {
        return DynamicSoapRequestBuilder.buildRequest(
                dynamicBinding.getSoapAction(),
                XmlTemplateBuilder.buildTemplate(wsdl, dynamicBinding),
                requestFields);
    }

    private void fillDynamicBinding(DynamicBinding dynamicBinding, Definitions wsdl) {
        wsdl.getLocalBindings()
                .forEach(
                        binding -> {
                            dynamicBinding.setNamespaceURI(
                                    binding.getDefinitions().getTargetNamespace());
                            dynamicBinding.setBindingName(binding.getName());
                            dynamicBinding.setPortType(binding.getTypePN().replace("tns:", ""));
                            dynamicBinding.setSoapAction(
                                    dynamicBinding
                                            .getNamespaceURI()
                                            .concat(dynamicBinding.getPortType())
                                            .concat("/")
                                            .concat(dynamicBinding.getOperation()));
                        });
    }
}
