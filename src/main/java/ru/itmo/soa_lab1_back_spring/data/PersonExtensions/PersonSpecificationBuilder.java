package ru.itmo.soa_lab1_back_spring.data.PersonExtensions;

import org.springframework.data.jpa.domain.Specification;
import ru.itmo.soa_lab1_back_spring.data.Person;
import ru.itmo.soa_lab1_back_spring.entities.DBPerson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersonSpecificationBuilder {
    private final List<SearchCriteria> params;

    public PersonSpecificationBuilder(){
        params = new ArrayList<SearchCriteria>();
    }

    public PersonSpecificationBuilder with(String key, String operation, Object value){
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<DBPerson> build() {
        if (params.size() == 0)
            return null;

        List<Specification<DBPerson>> specs = params.stream()
                .map(PersonSpecification::new)
                .collect(Collectors.toList());

        Specification<DBPerson> result = specs.get(0);

        for (int i = 1; i <params.size(); i++){
            result = Specification.where(result).and(specs.get(i));
        }

        return result;
    }
}
