package ru.practicum.event.model;

import lombok.*;

import javax.persistence.Embeddable;


@Embeddable
@Data
public class Location {
    private Float longitude;
    private Float latitude;
}
