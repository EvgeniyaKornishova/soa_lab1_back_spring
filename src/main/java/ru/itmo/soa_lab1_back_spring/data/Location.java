package ru.itmo.soa_lab1_back_spring.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.soa_lab1_back_spring.entities.DBLocation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {
    private Integer x; //Поле не может быть null
    private Float y;
    private Float z;

    public DBLocation toDBLocation(){return new DBLocation(x, y, z);}
}
