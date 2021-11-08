package ru.itmo.soa_lab1_back_spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.soa_lab1_back_spring.entities.DBLocation;

public interface LocationRepository extends JpaRepository<DBLocation, Long> {

}
