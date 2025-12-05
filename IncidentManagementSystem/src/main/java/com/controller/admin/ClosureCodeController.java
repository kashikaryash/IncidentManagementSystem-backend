package com.controller.admin;

import com.entity.ClosureCode;
import com.service.admin.ClosureCodeService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/admin/closure-codes")
public class ClosureCodeController {

    private final ClosureCodeService service;

    public ClosureCodeController(ClosureCodeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ClosureCode>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<ClosureCode> create(@RequestBody ClosureCode code) {
        ClosureCode created = service.create(code);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClosureCode> update(@PathVariable Long id, @RequestBody ClosureCode code) {
        ClosureCode updated = service.update(id, code);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<String> importFromCsv(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("name", "active", "isDefault")
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            for (CSVRecord record : records) {
                ClosureCode code = new ClosureCode();
                code.setName(record.get("name"));
                code.setActive(Boolean.parseBoolean(record.get("active")));
                code.setDefault(Boolean.parseBoolean(record.get("isDefault")));
                service.create(code);
            }

            return ResponseEntity.ok("CSV import successful");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to import CSV: " + e.getMessage());
        }
    }
}
