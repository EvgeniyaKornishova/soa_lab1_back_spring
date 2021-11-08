package ru.itmo.soa_lab1_back_spring.data.PersonExtensions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Paginator {
    private final Integer pageId;
    private final Integer pageSize;
}
