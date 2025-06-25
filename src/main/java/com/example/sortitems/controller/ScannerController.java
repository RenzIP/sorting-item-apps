package com.example.sortitems.controller;

import com.example.sortitems.model.Item;
import com.example.sortitems.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScannerController {

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("/save-barcode")
    public String saveBarcode(@RequestBody Map<String, String> request) {
        String barcode = request.get("barcode");

        if (barcode == null || barcode.isBlank()) {
            return "Barcode kosong!";
        }

        Item item = new Item();
        item.setName(barcode);
        item.setQuantity(1);
        item.setCategory("Umum");
        item.setImageUrl("");
        item.setDeleted(false);

        itemRepository.save(item);
        return "Barcode '" + barcode + "' berhasil disimpan!";
    }
}
