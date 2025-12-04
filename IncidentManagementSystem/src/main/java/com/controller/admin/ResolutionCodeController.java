package com.controller.admin;

import com.entity.ResolutionCode;
import com.service.admin.ResolutionCodeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resolution-codes")
@CrossOrigin(origins = "http://localhost:5173") 
public class ResolutionCodeController {

	private final ResolutionCodeService service;

	public ResolutionCodeController(ResolutionCodeService service) {
		this.service = service;
	}

	@GetMapping
	public List<ResolutionCode> getAll() {
		return service.getAll();
	}

	@PostMapping
	public ResponseEntity<ResolutionCode> create(@RequestBody ResolutionCode code) {
		return ResponseEntity.ok(service.create(code));
	}

	@PutMapping("/{id}/toggle")
	public ResponseEntity<ResolutionCode> toggleActive(@PathVariable Long id) {
		return ResponseEntity.ok(service.toggleActive(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<ResolutionCode> update(@PathVariable Long id, @RequestBody ResolutionCode updatedCode) {
		return ResponseEntity.ok(service.update(id, updatedCode.getCodeName(), updatedCode.isActive()));
	}

}
