package ru.itmo.soa_lab1_back_spring;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "server_response")
public class ServerResponse<T> {
    @XmlElement
    private T body;
}
