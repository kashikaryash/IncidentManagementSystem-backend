package com.service.admin;

import com.entity.Attachment;
import com.entity.Incident;
import com.repository.AttachmentRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public Attachment saveAttachment(MultipartFile file) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFileData(file.getBytes());
        return attachmentRepository.save(attachment);
    }

    public List<Attachment> saveAttachments(List<MultipartFile> files) throws IOException {
        List<Attachment> list = new ArrayList<>();
        if (files == null || files.isEmpty()) return list;
        for (MultipartFile mf : files) list.add(saveAttachment(mf));
        return list;
    }

    public Optional<Attachment> getAttachmentById(Long id) {
        return attachmentRepository.findById(id);
    }

    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll();
    }

    public ResponseEntity<byte[]> downloadAttachment(Long id) {
        return attachmentRepository.findById(id)
                .map(attachment -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(attachment.getFileType() != null ? attachment.getFileType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                        .body(attachment.getFileData()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
    }

    public void deleteAttachment(Long id) {
        if (!attachmentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found");
        }
        attachmentRepository.deleteById(id);
    }

    /**
     * Convenience: find attachments by incident id (incident or endUserIncident).
     * Implementations may vary depending on repository methods available.
     */
    public List<Attachment> getAttachmentsByIncidentId(Long incidentId) {
        return attachmentRepository.findByIncidentId(incidentId);
    }
    
    /**
     * Saves uploaded files and links them to an incident.
     */
    public void saveFiles(Incident incident, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        List<Attachment> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Attachment attachment = new Attachment();
                attachment.setIncident(incident);
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFileType(file.getContentType());
                attachment.setFileData(file.getBytes());

                attachments.add(attachment);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file: " + file.getOriginalFilename());
            }
        }

        attachmentRepository.saveAll(attachments);
        incident.setAttachments(attachments);
    }
}
