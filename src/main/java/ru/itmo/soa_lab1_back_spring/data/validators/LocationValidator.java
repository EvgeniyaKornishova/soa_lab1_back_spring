package ru.itmo.soa_lab1_back_spring.data.validators;

import ru.itmo.soa_lab1_back_spring.data.Location;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LocationValidator implements Validator<Location> {
    public List<String> validate(Location location) throws IllegalAccessException {
        List<String> errorList = new ArrayList<>();

        if (location == null) {
            return errorList;
        }

        for (Field f : Location.class.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.get(location) == null) {
                errorList.add(String.format("Location %s isn't specified or have a wrong type", f.getName()));
            }
        }

        return errorList;
    }
}
