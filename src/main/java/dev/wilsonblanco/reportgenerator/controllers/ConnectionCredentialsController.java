package dev.wilsonblanco.reportgenerator.controllers;

import dev.wilsonblanco.reportgenerator.dto.ConnectionCredentialsDto;
import dev.wilsonblanco.reportgenerator.dto.responses.GlobalResponse;
import dev.wilsonblanco.reportgenerator.services.ConnectionCredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connections")
public class ConnectionCredentialsController {

    @Autowired
    ConnectionCredentialsService connectionCredentialsService;

    @PostMapping
    public ResponseEntity<GlobalResponse> createConnection(@RequestBody ConnectionCredentialsDto request) {
        return connectionCredentialsService.createConnection(request);
    }

    @GetMapping("/{connectionUuid}")
    public ResponseEntity<GlobalResponse> getConnection(@PathVariable String connectionUuid) {
        return connectionCredentialsService.getConnection(connectionUuid);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse> getAllConnections() {
        return connectionCredentialsService.getConnectionList();
    }

    @DeleteMapping("/{connectionUuid}")
    public ResponseEntity<GlobalResponse> deleteConnection(@PathVariable String connectionUuid) {
        return connectionCredentialsService.deleteConnection(connectionUuid);
    }

    @PatchMapping
    public ResponseEntity<GlobalResponse> updateConnection(@RequestBody ConnectionCredentialsDto request) {
        return connectionCredentialsService.updateConnection(request);
    }
}
