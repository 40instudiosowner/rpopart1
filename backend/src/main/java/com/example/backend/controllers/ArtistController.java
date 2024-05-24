package com.example.backend.controllers;

import com.example.backend.models.Country;
import com.example.backend.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.models.Artist;
import com.example.backend.repositories.ArtistRepository;
import org.springframework.web.server.ResponseStatusException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/artists")
    public List
    getAllArtists() {
        return artistRepository.findAll();
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist)        throws Exception {
        try {
            Optional<Country>
                cc = countryRepository.findById(artist.country.id);
            if (cc.isPresent()) {
            artist.country = cc.get();        }
            if (artist.name.isEmpty() || artist.name.length() == org.springframework.util.StringUtils.countOccurrencesOf(artist.name, " "))
            {
                Map<String, String> map =  new HashMap<>();
                map.put("error", "Artist is empty");
                return ResponseEntity.ok(map);
            }        Artist nc = artistRepository.save(artist);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("countries.name_UNIQUE"))
                error = "countyalreadyexists";
            else
                error = "undefinederror";
            Map<String, String> map =  new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }


    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long ArtistId, @RequestBody Artist ArtistDetails) {
        Artist artist = null;
        Optional<Artist>
                cc = artistRepository.findById(ArtistId);
        if (cc.isPresent()) {
            artist = cc.get();
            artist.name = ArtistDetails.name;
            artistRepository.save(artist);
            return ResponseEntity.ok(artist);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
        }
    }

    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable(value = "id") Long ArtistId) {
        Optional<Artist>
                artist = artistRepository.findById(ArtistId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (artist.isPresent()) {
            artistRepository.delete(artist.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }
}