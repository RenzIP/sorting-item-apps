package com.example.sortitems.controller;

import com.example.sortitems.model.Item;
import com.example.sortitems.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/sort")
    public String showItems(Model model, @RequestParam(required = false) String sortBy) {
        List<Item> items;
        if ("quantity".equals(sortBy)) {
            items = itemRepository.findAllByOrderByQuantityAsc();
        } else {
            items = itemRepository.findAllByOrderByNameAsc();
        }

        // Debugging: Log items to check their IDs
        items.forEach(item -> System.out.println("Item ID: " + (item.getId() != null ? item.getIdAsString() : "null")));

        model.addAttribute("items", items);
        model.addAttribute("sortBy", sortBy);
        return "sort";
    }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addItem(@RequestParam String name, @RequestParam int quantity) {
        Item item = new Item(name, quantity);
        itemRepository.save(item);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Item added successfully!");
        response.put("items", getItems(null)); // Return updated list of items
        return response;
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteItem(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Received ID for deletion: " + id); // Debug log
        try {
            if (!ObjectId.isValid(id)) {
                response.put("status", "error");
                response.put("message", "Invalid ObjectId format: " + id);
                return response;
            }
            ObjectId objectId = new ObjectId(id);
            itemRepository.deleteById(objectId);
            response.put("status", "success");
            response.put("message", "Item deleted successfully!");
            response.put("items", getItems(null)); // Return updated list of items
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", "Invalid ObjectId: " + id);
        }
        return response;
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public Map<String, Object> updateItem(@PathVariable String id, @RequestParam String name, @RequestParam int quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!ObjectId.isValid(id)) {
                response.put("status", "error");
                response.put("message", "Invalid ObjectId format: " + id);
                return response;
            }
            Item item = itemRepository.findById(new ObjectId(id)).orElse(null);
            if (item != null) {
                item.setName(name);
                item.setQuantity(quantity);
                itemRepository.save(item);
                response.put("status", "success");
                response.put("message", "Item updated successfully!");
                response.put("items", getItems(null)); // Return updated list of items
            } else {
                response.put("status", "error");
                response.put("message", "Item not found!");
            }
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", "Invalid ObjectId: " + id);
        }
        return response;
    }

    private List<Item> getItems(String sortBy) {
        if ("quantity".equals(sortBy)) {
            return itemRepository.findAllByOrderByQuantityAsc();
        } else {
            return itemRepository.findAllByOrderByNameAsc();
        }
    }
}