package com.example.sortitems.controller;

import com.example.sortitems.model.ActivityLog;
import com.example.sortitems.model.Item;
import com.example.sortitems.repository.ItemRepository;
import com.example.sortitems.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.bson.types.ObjectId;
import com.example.sortitems.repository.ActivityLogRepository;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @PostConstruct
    public void init() {
        System.out.println("ItemController initialized - /sort/api endpoint should be available");
    }

    @GetMapping("/sort")
    public String showItems(@RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterByCategory,
            @RequestParam(required = false) Integer pageSize,
            Model model, HttpServletRequest request) {
        List<Item> items = getItems(sortBy, search, filterByCategory, pageSize);
        System.out.println("All items before filtering: " + items);

        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            System.out.println("Search term: " + searchLower);
            items = items.stream()
                    .filter(item -> item.getName() != null && !item.getName().trim().isEmpty()
                            && item.getName().toLowerCase().startsWith(searchLower))
                    .collect(Collectors.toList());
            System.out.println("Filtered items: " + items);
        }

        if ("quantity".equalsIgnoreCase(sortBy)) {
            items = bubbleSortByQuantity(items);
        } else if ("category".equalsIgnoreCase(sortBy)) {
            items = bubbleSortByCategory(items);
        } else {
            items = bubbleSortByName(items);
        }

        model.addAttribute("items", items);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("search", search);
        model.addAttribute("filterByCategory", filterByCategory);
        model.addAttribute("pageSize", pageSize);
        return "sort";
    }

    @GetMapping(value = "/sort/api", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getItemsApi(@RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterByCategory,
            @RequestParam(required = false) Integer pageSize) {
        System.out.println("API call - Method getItemsApi invoked for /sort/api");
        List<Item> items = getItems(sortBy, search, filterByCategory, pageSize);
        System.out.println("API call - All items before filtering: " + items);

        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            System.out.println("API call - Search term: " + searchLower);
            items = items.stream()
                    .filter(item -> item.getName() != null && !item.getName().trim().isEmpty()
                            && item.getName().toLowerCase().startsWith(searchLower))
                    .collect(Collectors.toList());
            System.out.println("API call - Filtered items: " + items);
        }

        if ("quantity".equalsIgnoreCase(sortBy)) {
            items = bubbleSortByQuantity(items);
        } else if ("category".equalsIgnoreCase(sortBy)) {
            items = bubbleSortByCategory(items);
        } else {
            items = bubbleSortByName(items);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        System.out.println("API call - Response: " + response);
        return response;
    }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addItem(@RequestParam String name, @RequestParam int quantity,
            @RequestParam(required = false) String imageUrl, @RequestParam(required = false) String category) {
        Item item = new Item(name, quantity, imageUrl, category);
        itemRepository.save(item);
        // Catat aktivitas
        activityLogService.logActivity("ADD", item.getId().toString(), item.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Item added successfully!");
        response.put("items", getItems(null, null, null, null));
        return response;
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteItem(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Received ID for deletion: " + id);
        try {
            if (!ObjectId.isValid(id)) {
                response.put("status", "error");
                response.put("message", "Invalid ObjectId format: " + id);
                return response;
            }
            ObjectId objectId = new ObjectId(id);
            Item item = itemRepository.findById(objectId).orElse(null);
            if (item != null) {
                String itemName = item.getName();
                item.setDeleted(true);
                itemRepository.save(item); // Simpan perubahan
                activityLogService.logActivity("DELETE", id, itemName + " (trashed)");
            }
            response.put("status", "success");
            response.put("message", "Item deleted successfully!");
            response.put("items", getItems(null, null, null, null));
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", "Invalid ObjectId: " + id);
        }
        return response;
    }

    @GetMapping("/trash")
    public String showTrash(Model model) {
        List<Item> trashedItems = itemRepository.findAll()
                .stream()
                .filter(Item::isDeleted)
                .collect(Collectors.toList());

        model.addAttribute("items", trashedItems);
        return "trash"; // Buat file `trash.html`
    }

    @PostMapping("/restore/{id}")
    @ResponseBody
    public Map<String, Object> restoreItem(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!ObjectId.isValid(id)) {
                response.put("status", "error");
                response.put("message", "Invalid ObjectId format: " + id);
                return response;
            }
            Item item = itemRepository.findById(new ObjectId(id)).orElse(null);
            if (item != null && item.isDeleted()) {
                item.setDeleted(false);
                itemRepository.save(item);
                activityLogService.logActivity("RESTORE", id, item.getName());
                response.put("status", "success");
                response.put("message", "Item restored successfully!");
            } else {
                response.put("status", "error");
                response.put("message", "Item not found or not deleted!");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error restoring item.");
        }
        return response;
    }

    @PostMapping("/trash/delete/{id}")
    @ResponseBody
    public Map<String, Object> permanentDelete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!ObjectId.isValid(id)) {
                response.put("status", "error");
                response.put("message", "Invalid ObjectId");
                return response;
            }
            itemRepository.deleteById(new ObjectId(id));
            response.put("status", "success");
            response.put("message", "Item permanently deleted!");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Deletion failed!");
        }
        return response;
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public Map<String, Object> updateItem(@PathVariable String id, @RequestParam String name,
            @RequestParam int quantity, @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String category) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!ObjectId.isValid(id)) {
                response.put("status", "error");
                response.put("message", "Invalid ObjectId format: " + id);
                return response;
            }
            Item item = itemRepository.findById(new ObjectId(id)).orElse(null);
            if (item != null) {
                String oldName = item.getName();
                item.setName(name);
                item.setQuantity(quantity);
                item.setImageUrl(imageUrl);
                item.setCategory(category);
                itemRepository.save(item);
                // Catat aktivitas
                activityLogService.logActivity("EDIT", id, oldName + " -> " + name);
                response.put("status", "success");
                response.put("message", "Item updated successfully!");
                response.put("items", getItems(null, null, null, null));
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

    @GetMapping("/history") // Add this endpoint
    public String showHistory(Model model) {
        List<ActivityLog> logs = activityLogRepository.findAll();
        model.addAttribute("logs", logs);
        return "history";
    }

    private List<Item> getItems(String sortBy, String search, String filterByCategory, Integer pageSize) {
        List<Item> items = itemRepository.findAll()
                .stream()
                .filter(item -> !item.isDeleted()) // Filter out deleted items
                .collect(Collectors.toList());

        if (filterByCategory != null && !filterByCategory.trim().isEmpty()
                && !filterByCategory.equalsIgnoreCase("All Categories")) {
            String filterCategoryLower = filterByCategory.toLowerCase();
            System.out.println("Filtering by category: " + filterCategoryLower);
            items = items.stream()
                    .filter(item -> item.getCategory() != null &&
                            item.getCategory().equalsIgnoreCase(filterCategoryLower))
                    .collect(Collectors.toList());
            System.out.println("Items after category filter: " + items.size());
        }

        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            items = items.stream()
                    .filter(item -> item.getName() != null && !item.getName().trim().isEmpty()
                            && item.getName().toLowerCase().startsWith(searchLower))
                    .collect(Collectors.toList());
        }

        if ("quantity".equals(sortBy)) {
            items = bubbleSortByQuantity(items);
        } else if ("category".equals(sortBy)) {
            items = bubbleSortByCategory(items);
        } else {
            items = bubbleSortByName(items);
        }
        if (pageSize != null && pageSize > 0 && items.size() > pageSize) {
            items = new ArrayList<>(items.subList(0, pageSize));
        }
        return items;
    }

    private List<Item> bubbleSortByName(List<Item> items) {
        int n = items.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (items.get(j).getName().compareToIgnoreCase(items.get(j + 1).getName()) > 0) {
                    Item temp = items.get(j);
                    items.set(j, items.get(j + 1));
                    items.set(j + 1, temp);
                }
            }
        }
        return items;
    }

    private List<Item> bubbleSortByQuantity(List<Item> items) {
        int n = items.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (items.get(j).getQuantity() > items.get(j + 1).getQuantity()) {
                    Item temp = items.get(j);
                    items.set(j, items.get(j + 1));
                    items.set(j + 1, temp);
                }
            }
        }
        return items;
    }

    private List<Item> bubbleSortByCategory(List<Item> items) {
        int n = items.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                String category1 = items.get(j).getCategory();
                String category2 = items.get(j + 1).getCategory();

                // Handle null categories to avoid NullPointerException
                // Treat nulls as coming before non-nulls, or handle as per your preference
                int comparison;
                if (category1 == null && category2 == null)
                    comparison = 0;
                else if (category1 == null)
                    comparison = -1; // category1 comes first
                else if (category2 == null)
                    comparison = 1; // category2 comes first
                else
                    comparison = category1.compareToIgnoreCase(category2);

                if (comparison > 0) {
                    Item temp = items.get(j);
                    items.set(j, items.get(j + 1));
                    items.set(j + 1, temp);
                }
            }
        }
        return items;
    }
}