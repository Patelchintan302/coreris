package com.example.coreris.controller;

import com.example.coreris.dto.ReceptionistDto;
import com.example.coreris.service.ReceptionistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receptionists")
public class ReceptionistController {
    private final ReceptionistService receptionistService;

    //sp note :- accessible to any authenticated user
    @GetMapping
    public ResponseEntity<List<ReceptionistDto>> getAllReceptionists(){
        return ResponseEntity.ok(receptionistService.getAllReceptionists());
    }

    //sp note :- accessible to any authenticated user
    @PutMapping("/{id}")
    public ResponseEntity<ReceptionistDto> updateReceptionist(@PathVariable Long id,@RequestBody ReceptionistDto dto){
        return ResponseEntity.ok(receptionistService.updateReceptionist(id,dto));
    }
}
