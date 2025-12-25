package dev.wilsonblanco.reportgenerator.controllers;

import dev.wilsonblanco.reportgenerator.services.DataBaseConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/database")
public class DataBaseController {

    @Autowired
    private DataBaseConnectionService connectionService;

    @GetMapping("/tables/{connectionUuid}")
    public ResponseEntity<List<String>> getTables(@PathVariable String connectionUuid) throws Exception {
            return connectionService.listTables(connectionUuid);
    }


}
