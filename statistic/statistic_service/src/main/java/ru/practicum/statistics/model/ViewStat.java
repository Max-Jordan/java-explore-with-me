package ru.practicum.statistics.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewStat {
    private String app;
    private String uri;
    private long hits;
}