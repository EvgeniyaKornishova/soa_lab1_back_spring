package ru.itmo.soa_lab1_back_spring.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.soa_lab1_back_spring.data.Location;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;

@NoArgsConstructor
@Entity
@Getter
@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "location")
public class DBLocation {

    public DBLocation(Integer x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Id
    @XmlTransient
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // в модели отсутствует

    @Column(nullable = false)
    private Integer x; //Поле не может быть null

    private float y;

    private float z;

    public void update(Location data) {
        this.x = data.getX();
        this.y = data.getY();
        this.z = data.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBLocation that = (DBLocation) o;
        return id == that.id && Float.compare(that.y, y) == 0 && Float.compare(that.z, z) == 0 && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, z);
    }
}
