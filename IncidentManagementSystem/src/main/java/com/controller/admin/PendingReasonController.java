package com.controller.admin;

import com.entity.PendingReason;
import com.service.admin.PendingReasonService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/pending-reasons")
public class PendingReasonController {

	private final PendingReasonService service;

	public PendingReasonController(PendingReasonService service) {
		this.service = service;
	}

	@GetMapping
	public List<PendingReason> getAllReasons() {
		return service.getAllReasons();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PendingReason> getReasonById(@PathVariable Long id) {
		return service.getReasonById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<PendingReason> createReason(@RequestBody PendingReason reason) {
		return ResponseEntity.ok(service.createReason(reason));
	}

	@PutMapping("/{id}")
	public ResponseEntity<PendingReason> updateReason(@PathVariable Long id, @RequestBody PendingReason reason) {
		return ResponseEntity.ok(service.updateReason(id, reason));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReason(@PathVariable Long id) {
		service.deleteReason(id);
		return ResponseEntity.noContent().build();
	}
}
