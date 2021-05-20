package com.soap.connector.utils;

import com.predic8.wsdl.Definitions;
import com.predic8.wstool.creator.RequestTemplateCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import com.soap.connector.domain.DynamicBinding;
import com.soap.connector.domain.DynamicXmlTemplate;
import groovy.xml.MarkupBuilder;
import java.io.StringWriter;
import lombok.experimental.UtilityClass;
import org.json.XML;

@UtilityClass
public class XmlTemplateBuilder {

    public DynamicXmlTemplate buildTemplate(Definitions wsdl, DynamicBinding dynamicBinding) {
        StringWriter writer = new StringWriter();
        SOARequestCreator creator =
                new SOARequestCreator(
                        wsdl, new RequestTemplateCreator(), new MarkupBuilder(writer));
        String requestTemplate =
                wsdl.getElement("tns:".concat(dynamicBinding.getObjectRequest()))
                        .getRequestTemplate();
        creator.createRequest(
                dynamicBinding.getPortType(),
                dynamicBinding.getOperation(),
                dynamicBinding.getBindingName());
        return DynamicXmlTemplate.builder()
                .requestTemplate(requestTemplate)
                .xmlTemplate(XML.toJSONObject(requestTemplate).toMap())
                .build();
    }
}
