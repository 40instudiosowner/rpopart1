package ru.iu3.backend.controllers;
import ru.iu3.backend.models.Museum;

import ru.iu3.backend.repositories.MuseumRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;


import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@CrossOrigin("")
@RequestMapping("/api/v1")
public class MuseumController {
    @Autowired
    MuseumRepository museumRepository;

    @PostMapping("/museums")
    public ResponseEntity<Object> createMuseum(@RequestBody Museum museum)
            throws Exception {
        try {
            Museum nc = museumRepository.save(museum);
            System.out.println(nc.name);
            return ResponseEntity.ok(nc);
        } catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("museums.name_UNIQUE"))
                error = "useralreadyexists";
            else
                error = "undefinederror";
            Map<String, String>
                    map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }

    @GetMapping("/museums")
    public Page<Museum> getAllMuseums(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return museumRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @DeleteMapping("/deletemuseums")
    public ResponseEntity<HttpStatus> deleteMuseums(@RequestBody List<Museum> museums) {
        List<Long> listOfIds = new ArrayList<>();
        for (Museum artist: museums){
            listOfIds.add(artist.id);
        }
        museumRepository.deleteAllById(listOfIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/museums/{id}")
    public ResponseEntity<Museum> updateMuseum(@PathVariable(value = "id") Long museumId,
                                               @RequestBody Museum museum) {
        Museum mus = null;
        Optional<Museum> cc = museumRepository.findById(museumId);
        if (cc.isPresent()) {
            mus = cc.get();
            mus.name = museum.name;
            mus.location = museum.location;
            museumRepository.save(mus);
            return ResponseEntity.ok(mus);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "museum not found");
        }
    }

}
