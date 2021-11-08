package ru.itmo.soa_lab1_back_spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.itmo.soa_lab1_back_spring.ServerResponse;
import ru.itmo.soa_lab1_back_spring.XMLUtils.XMLConverter;
import ru.itmo.soa_lab1_back_spring.data.*;
import ru.itmo.soa_lab1_back_spring.data.PersonExtensions.Paginator;
import ru.itmo.soa_lab1_back_spring.data.Person;
import ru.itmo.soa_lab1_back_spring.data.PersonExtensions.PersonList;
import ru.itmo.soa_lab1_back_spring.data.PersonExtensions.PersonSpecificationBuilder;
import ru.itmo.soa_lab1_back_spring.data.validators.PaginatorValidator;
import ru.itmo.soa_lab1_back_spring.data.validators.PersonValidator;
import ru.itmo.soa_lab1_back_spring.data.validators.SorterValidator;
import ru.itmo.soa_lab1_back_spring.entities.DBLocation;
import ru.itmo.soa_lab1_back_spring.entities.DBPerson;
import ru.itmo.soa_lab1_back_spring.repositories.LocationRepository;
import ru.itmo.soa_lab1_back_spring.repositories.PersonRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@Transactional
@RequestMapping("/persons")
public class PersonController {
    private final PersonValidator personValidator;
    private final PaginatorValidator paginatorValidator;
    private final SorterValidator sorterValidator;
//    private final FilterValidator filterValidator;
    private final XMLConverter converter;

    @Autowired PersonRepository personRepository;
    @Autowired LocationRepository locationRepository;

    public PersonController(){
        personValidator = new PersonValidator();
        paginatorValidator = new PaginatorValidator();
        sorterValidator = new SorterValidator();
//        filterValidator = new FilterValidator();
        converter = new XMLConverter();
    }


    @GetMapping("")
    public ResponseEntity<String> getPersonList(
            // sort
            @RequestParam(name="sort", required = false) Optional<String> sort,
            // pagination
            @RequestParam(name="page_id", required = false, defaultValue = "1") Integer pageId,
            @RequestParam(name="page_size", required = false) Optional<Integer> pageSize,
            // filters
            @RequestParam(name="name", required = false) Optional<String> name,
            @RequestParam(name="height", required = false) Optional<Float> height,
            @RequestParam(name="eye_color", required = false) Optional<Color> eyeColor,
            @RequestParam(name="hair_color", required = false) Optional<Color> hairColor,
            @RequestParam(name="nationality", required = false) Optional<Country> nationality,
            @RequestParam(name="coordinates_x", required = false) Optional<Integer> coordinatesX,
            @RequestParam(name="coordinates_y", required = false) Optional<Double> coordinatesY,
            @RequestParam(name="location_x", required = false) Optional<Integer> locationX,
            @RequestParam(name="location_y", required = false) Optional<Float> locationY,
            @RequestParam(name="location_z", required = false) Optional<Float> locationZ,
            @RequestParam(name="creation_date", required = false) Optional<String> sCreationDate
    ){
        boolean sorting = false;

        if (!pageSize.isPresent()) {
            pageSize = Optional.of((int) personRepository.count());
            if (pageSize.get() == 0){
                PersonList personList = new PersonList(0L, new ArrayList<>());
                return new ResponseEntity<>(converter.toStr(personList), HttpStatus.OK);
            }
        }

        Paginator paginator = new Paginator(pageId, pageSize.get());

        List<String> errors = paginatorValidator.validate(paginator);
        if (!errors.isEmpty())
            return new ResponseEntity<>(converter.listToStr(errors, "errors", new String[0]), HttpStatus.BAD_REQUEST);

        if (sort.isPresent()) {
            errors = sorterValidator.validate(sort.get());
            if (!errors.isEmpty())
                return new ResponseEntity<>(converter.listToStr(errors, "errors", new String[0]), HttpStatus.BAD_REQUEST);
            sorting = true;
        }

        Pageable sortAndPaging;
        if (sorting){
            sortAndPaging = PageRequest.of(paginator.getPageId()-1, paginator.getPageSize(), Sort.by(sort.get()));
        }else{
            sortAndPaging = PageRequest.of(paginator.getPageId()-1, paginator.getPageSize());
        }

        Optional<LocalDateTime> creationDate = Optional.empty();
        if (sCreationDate.isPresent())
            try {
                creationDate = Optional.of(LocalDateTime.parse(sCreationDate.get()));
            }catch (DateTimeParseException e){
                return new ResponseEntity<>("Invalid creation_date format", HttpStatus.BAD_REQUEST);
            }

//        errors = filterValidator.validate();
//        if (!errors.isEmpty())
//            return new ResponseEntity<String>(converter.listToStr(errors, "errors", new String[0]), HttpStatus.BAD_REQUEST);
//
//        PersonFilter filter = new PersonFilter(request);

        PersonSpecificationBuilder builder = new PersonSpecificationBuilder();

        name.ifPresent(x -> builder.with("name", "=", x));
        height.ifPresent(x -> builder.with("height", "=", x));
        eyeColor.ifPresent(x -> builder.with("eyeColor", "=", x));
        hairColor.ifPresent(x -> builder.with("hairColor", "=", x));
        nationality.ifPresent(x -> builder.with("nationality", "=", x));
        creationDate.ifPresent(x -> builder.with("creationDate", "=", x));
        coordinatesX.ifPresent(x -> builder.with("coordinates.x", "=", x));
        coordinatesY.ifPresent(x -> builder.with("coordinates.y", "=", x));
        locationX.ifPresent(x -> builder.with("location.x", "=", x));
        locationY.ifPresent(x -> builder.with("location.y", "=", x));
        locationZ.ifPresent(x -> builder.with("location.z", "=", x));

        Specification<DBPerson> spec = builder.build();

        Page<DBPerson> dbPersonList = personRepository.findAll(spec, sortAndPaging);
        Long count = personRepository.count();

        PersonList personList = new PersonList(count, dbPersonList.getContent());

        return new ResponseEntity<>(converter.toStr(personList), HttpStatus.OK);
    }


    @GetMapping("/{person_id}")
    public ResponseEntity<String> getPerson(@PathVariable("person_id") Long id){
        if (id == null)
            return new ResponseEntity<>("Person with specified id not found", HttpStatus.NOT_FOUND);

        Optional<DBPerson> opDbPerson = personRepository.findById(id);
        if (!opDbPerson.isPresent())
            return new ResponseEntity<>("Person with specified id not found", HttpStatus.NOT_FOUND);

        DBPerson dbPerson = opDbPerson.get();

        return new ResponseEntity<>(converter.toStr(dbPerson), HttpStatus.OK);
    }

    private boolean compareLocations(DBLocation left, DBLocation right){
        return (Float.compare(left.getY(), right.getY()) == 0 && Float.compare(left.getZ(), right.getZ()) == 0 && Objects.equals(left.getX(), right.getX()));
    }

    @GetMapping("/locations/uniq")
    public ResponseEntity<String> getUniqLocations(){
       List<DBLocation> locations = locationRepository.findAll();

       List<DBLocation> uniq_locations = new java.util.ArrayList<>();

       for (DBLocation loc : locations) {
           boolean flag = true;
           for (DBLocation uniq_loc : uniq_locations) {
               if (compareLocations(loc, uniq_loc))
                   flag = false;
           }
           if (flag)
               uniq_locations.add(loc);
       }

        return new ResponseEntity<>(converter.listToStr(uniq_locations, "locations", new DBLocation[0]), HttpStatus.OK);
    }

    @GetMapping("/heights/sum")
    public ResponseEntity<String> getHeightsSum() {
        Float sumHeight = personRepository.calcSumHeight();
        if (sumHeight == null)
            sumHeight = (float) 0;

        return new ResponseEntity<>(converter.toStr(new ServerResponse<Float>(sumHeight)), HttpStatus.OK);
    }


    @GetMapping("/names/search")
    public ResponseEntity<String> getPersonByName(@RequestParam(name="name") String name){
        if (name == null)
            return new ResponseEntity<>("Get parameter 'name' must be specified", HttpStatus.BAD_REQUEST);

        List<DBPerson> dbPersonList = personRepository.findByNameContains(name);

        return new ResponseEntity<>(converter.listToStr(dbPersonList, "persons", new DBPerson[0]), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> createPerson(@RequestBody Person person){
        try{
            List<String> errors = personValidator.validate(person);
            if (!errors.isEmpty())
                return new ResponseEntity<>(converter.listToStr(errors, "errors", new String[0]), HttpStatus.BAD_REQUEST);
        }catch (IllegalAccessException e){
            return new ResponseEntity<>("Failed to validate person parameters", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        DBPerson newDBPerson = person.toDBPerson();
        personRepository.save(newDBPerson);
        long id = newDBPerson.getId();

        return new ResponseEntity<>(converter.toStr(new ServerResponse<Long>(id)), HttpStatus.CREATED);
    }


    @PutMapping("/{person_id}")
    public ResponseEntity<String> updatePerson(@RequestBody Person person, @PathVariable("person_id") Long id){
        if (id == null)
            return new ResponseEntity<>("Person with specified id not found", HttpStatus.NOT_FOUND);

        try{
            List<String> errors = personValidator.validate(person);
            if (!errors.isEmpty())
                return new ResponseEntity<>(converter.listToStr(errors, "errors", new String[0]), HttpStatus.BAD_REQUEST);
        }catch (IllegalAccessException e){
            return new ResponseEntity<>("Failed to validate person parameters", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<DBPerson> opDbPerson = personRepository.findById(id);

        if (!opDbPerson.isPresent())
            return new ResponseEntity<>("Person with specified id not found", HttpStatus.NOT_FOUND);

        DBPerson dbPerson = opDbPerson.get();

        dbPerson.update(person);
        personRepository.save(dbPerson);

        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{person_id}")
    public ResponseEntity<String> deletePerson(@PathVariable("person_id") Long id){
        if (id == null)
            return new ResponseEntity<>("Person with specified id not found", HttpStatus.NOT_FOUND);

        Optional<DBPerson> person = personRepository.findById(id);

        if (!person.isPresent())
            return new ResponseEntity<>("Person with specified id not found", HttpStatus.NOT_FOUND);

        personRepository.delete(person.get());

        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }


}
