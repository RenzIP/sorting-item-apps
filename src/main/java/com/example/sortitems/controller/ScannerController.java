package com.example.sortitems.controller;

import com.example.sortitems.model.Item;
import com.example.sortitems.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScannerController {

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("/save-barcode")
    public ResponseEntity<Map<String, Object>> saveBarcode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String barcode = request.get("barcode");

        if (barcode == null || barcode.isBlank()) {
            response.put("status", "error");
            response.put("message", "Barcode kosong!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // QR code berisi string JSON → konversi ke Item
            ObjectMapper objectMapper = new ObjectMapper();
            Item item = objectMapper.readValue(barcode, Item.class);

            // Validasi data item
            if (item.getName() == null || item.getName().trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Nama item tidak boleh kosong!");
                return ResponseEntity.badRequest().body(response);
            }

            // Set nilai default opsional
            if (item.getImageUrl() == null)
                item.setImageUrl("");
            if (item.getCategory() == null)
                item.setCategory("Umum");
            if (item.getQuantity() <= 0)
                item.setQuantity(1);
            item.setDeleted(false);

            // Cek apakah item dengan nama yang sama sudah ada
            Item existingItem = itemRepository.findByNameAndDeletedFalse(item.getName());
            if (existingItem != null) {
                // Update quantity jika item sudah ada
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                itemRepository.save(existingItem);
                response.put("status", "success");
                response.put("message",
                        "Item sudah ada! Quantity berhasil ditambahkan. Total: " + existingItem.getQuantity());
                response.put("item", existingItem);
            } else {
                // Simpan item baru
                Item savedItem = itemRepository.save(item);
                response.put("status", "success");
                response.put("message", "Item baru berhasil disimpan dari QR!");
                response.put("item", savedItem);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Gagal memproses QR: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/scan-regular-barcode")
    public ResponseEntity<Map<String, Object>> scanRegularBarcode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String barcode = request.get("barcode");
        String itemName = request.get("itemName");
        String category = request.get("category");
        Integer quantity = Integer.parseInt(request.getOrDefault("quantity", "1"));

        if (barcode == null || barcode.isBlank()) {
            response.put("status", "error");
            response.put("message", "Barcode kosong!");
            return ResponseEntity.badRequest().body(response);
        }

        if (itemName == null || itemName.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Nama item tidak boleh kosong!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Cek apakah item dengan barcode yang sama sudah ada
            Item existingItem = itemRepository.findByNameAndDeletedFalse(itemName);
            if (existingItem != null) {
                // Update quantity jika item sudah ada
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                itemRepository.save(existingItem);
                response.put("status", "success");
                response.put("message",
                        "Item sudah ada! Quantity berhasil ditambahkan. Total: " + existingItem.getQuantity());
                response.put("item", existingItem);
            } else {
                // Buat item baru
                Item newItem = new Item();
                newItem.setName(itemName);
                newItem.setQuantity(quantity);
                newItem.setCategory(category != null ? category : "Umum");
                newItem.setImageUrl("");
                newItem.setDeleted(false);

                Item savedItem = itemRepository.save(newItem);
                response.put("status", "success");
                response.put("message", "Item baru berhasil disimpan!");
                response.put("item", savedItem);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Gagal menyimpan item: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
