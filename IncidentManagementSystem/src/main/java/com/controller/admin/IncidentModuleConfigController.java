package com.controller.admin;

import com.entity.IncidentModuleConfig;
import com.service.admin.IncidentModuleConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/config")
public class IncidentModuleConfigController {

    @Autowired
    private IncidentModuleConfigService configService;

    @GetMapping("/getConfig")
    public Optional<IncidentModuleConfig> getConfig() {
        return configService.getConfig();
    }

    @PostMapping("/update")
    public IncidentModuleConfig updateConfig(@RequestBody IncidentModuleConfig config) {
        return configService.saveOrUpdateConfig(config);
    }
}
