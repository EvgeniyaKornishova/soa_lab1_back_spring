package ru.itmo.soa_lab1_back_spring.data.PersonExtensions;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.itmo.soa_lab1_back_spring.entities.DBPerson;

import javax.persistence.criteria.*;

@AllArgsConstructor
public class PersonSpecification implements Specification<DBPerson> {
    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<DBPerson> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String operator = criteria.getOperation();

        String[] path = criteria.getKey().split("\\.");

        Path<?> field = root;
        for (String fieldName : path)
            field = field.get(fieldName);


        System.out.println("Field type is: " + field.getJavaType().toString());
        if (operator.equals("="))
            if (field.getJavaType() == String.class) {
                Path<String> s_field;
                if (path.length == 0)
                    s_field = root.<String>get(path[0]);
                else {
                    field = root;
                    for (int i = 1; i < path.length - 1; i++)
                        field = field.get(path[i]);

                    s_field = field.<String>get(path[path.length-1]);
                }
                System.out.println("String value: " + criteria.getValue().toString());
                return builder.like(s_field, "%" + criteria.getValue().toString() + "%");
            }
            else if (field.getJavaType() == float.class || field.getJavaType() == Double.class || field.getJavaType() == Float.class) {
                Path<String> s_field;
                if (path.length == 0)
                    s_field = root.<String>get(path[0]);
                else {
                    field = root;
                    for (int i = 0; i < path.length - 1; i++)
                        field = field.get(path[i]);

                    s_field = field.<String>get(path[path.length-1]);
                }
                System.out.println("float value: " + criteria.getValue().toString());
                return builder.equal(s_field, criteria.getValue().toString());
            }
            else
                return builder.equal(field, criteria.getValue());

        return null;
    }
}

