package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusRequestUpdateResponse {

    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
