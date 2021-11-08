package ru.itmo.soa_lab1_back_spring.data.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SorterValidator implements Validator<String> {
    private static final List<String> possibleValues = new ArrayList<>(Arrays.asList(
            "id",
            "name",
            "height",
            "eye_color",
            "hair_color",
            "nationality",
            "creation_date",
            "location.x",
            "location.y",
            "location.z",
            "coordinates.x",
            "coordinates.y"
    ));

    @Override
    public List<String> validate(String sort) {
        List<String> errorList = new ArrayList<>();

        if (sort != null && !possibleValues.contains(sort))
            errorList.add("unknown sort field");

        return errorList;
    }
}
