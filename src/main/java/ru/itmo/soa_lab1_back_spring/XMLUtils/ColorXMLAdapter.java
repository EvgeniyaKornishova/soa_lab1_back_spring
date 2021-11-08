package ru.itmo.soa_lab1_back_spring.XMLUtils;

import ru.itmo.soa_lab1_back_spring.data.Color;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ColorXMLAdapter extends XmlAdapter<String, Color> {
    @Override
    public Color unmarshal(String s) throws Exception {
            return Color.valueOf(s);
    }

    @Override
    public String marshal(Color color) throws Exception {
        return color.toString();
    }
}
