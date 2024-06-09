package ru.iu3.backend.controllers;

import ru.iu3.backend.models.Country;
import ru.iu3.backend.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.repositories.ArtistRepository;
import org.springframework.web.server.ResponseStatusException;


import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ArtistController {

    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/artists")
    public Page<Artist> getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return artistRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) {
        String error = "";
        Map<String, String> map = new HashMap<>();
        try {
        if(artist.country!=null) {

            Optional<Country> cc = countryRepository.findById(artist.country.id);
            if (cc.isPresent()) {
                artist.country = cc.get();
                Optional<Artist> artistOptional = artistRepository.findByName(artist.name);
                if (artistOptional.isEmpty()) {
                    artistRepository.save(artist);
                    return ResponseEntity.ok(artist);
                } else {
                    error = "Artist already exist";
                    map.put("error", error);

                }
            }
        }
            else{
                error = "There is no country with this name";
                map.put("error", error);
                return ResponseEntity.ok(map);
            }

        }
        catch (Exception e){
        error = "undefinederror";
        map.put("error", error);
        return ResponseEntity.ok(map);
    }
        return ResponseEntity.ok(map);
}
    @PostMapping("/deleteartists")
    public ResponseEntity<HttpStatus> deleteArtists(@RequestBody List<Artist> artists) {
        System.out.println(artists.get(0).toString());
        List<Long> listOfIds = new ArrayList<>();
        for (Artist artist : artists) {
            listOfIds.add(artist.id);
        }
        artistRepository .deleteAllById(listOfIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long artistId,
                                               @RequestBody Artist artist) {
        Artist newArtist;
        Optional<Artist> cc = artistRepository.findById(artistId);

        if (cc.isPresent()) {
            Optional<Country>
                    newArtistCountry = countryRepository.findById(cc.get().id);
            newArtistCountry.ifPresent(country -> artist.country = country);
            newArtist = cc.get();
            newArtist.name = artist.name;
            newArtist.country = artist.country;
            newArtist.century = artist.century;
            System.out.println(newArtist.country.name);
            artistRepository.save(newArtist);
            return ResponseEntity.ok(newArtist);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "artist not found");
        }
    }
}