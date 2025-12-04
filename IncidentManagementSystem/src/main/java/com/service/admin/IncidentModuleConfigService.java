package com.service.admin;

import com.entity.IncidentModuleConfig;
import com.repository.IncidentModuleConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IncidentModuleConfigService {

    private final IncidentModuleConfigRepository configRepository;

    public IncidentModuleConfigService(IncidentModuleConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public IncidentModuleConfig saveOrUpdateConfig(IncidentModuleConfig config) {
        return configRepository.save(config);
    }

    public Optional<IncidentModuleConfig> getConfig() {
        return configRepository.findById(1L);
    }
}
