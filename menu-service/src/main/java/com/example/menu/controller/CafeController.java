package com.example.menu.controller;

import com.example.menu.model.Cafe;
import com.example.menu.repository.CafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class CafeController {

    @Autowired
    private CafeRepository cafeMenuRepository;

    @GetMapping
    public List<Cafe> getAllMenus() {
        return cafeMenuRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cafe> getMenuById(@PathVariable Long id) {
        return cafeMenuRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cafe createMenu(@RequestBody Cafe menu) {
        return cafeMenuRepository.save(menu);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cafe> updateMenu(@PathVariable Long id, @RequestBody Cafe menu) {
        return cafeMenuRepository.findById(id)
                .map(existing -> {
                    menu.setId(id);
                    return ResponseEntity.ok(cafeMenuRepository.save(menu));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long id) {
        return cafeMenuRepository.findById(id)
                .map(menu -> {
                    cafeMenuRepository.delete(menu);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
