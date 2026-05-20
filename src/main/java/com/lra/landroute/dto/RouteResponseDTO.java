package com.lra.landroute.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteResponseDTO {

    private List<String> route;
}
