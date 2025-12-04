package com.dto.enduser;

import java.util.List;
import com.entity.Incident;

public interface EndUserIncidentServiceInterface {

    Incident create(Incident incident);

    Incident getById(Long id);

    List<Incident> getAll();

    Incident update(Long id, Incident updated);

    void delete(Long id);

    Incident createEndUserIncident(EndUserIncidentRequest request);

    List<Incident> findByCreatedBy(String username);
}
