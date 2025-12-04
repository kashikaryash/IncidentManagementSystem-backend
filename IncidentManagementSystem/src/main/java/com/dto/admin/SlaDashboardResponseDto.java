package com.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import com.dto.analyst.IncidentResponseDto;

@Data
@AllArgsConstructor
public class SlaDashboardResponseDto {
    private double averageResponseTime;
    private double averageResolutionTime;
    private int incidentsBreachingSLA;
    private int totalIncidentsTracked;
    private List<IncidentResponseDto> approachingSLA;
}
