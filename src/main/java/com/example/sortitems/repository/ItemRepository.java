package com.example.sortitems.repository;

import com.example.sortitems.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, ObjectId> {
    List<Item> findAllByOrderByNameAsc();

    List<Item> findAllByOrderByQuantityAsc();

    // Method untuk mencari item berdasarkan nama dan status deleted
    Item findByNameAndDeletedFalse(String name);

    // Method untuk mencari semua item yang tidak dihapus
    List<Item> findAllByDeletedFalse();

    // Method untuk mencari item berdasarkan kategori dan tidak dihapus
    List<Item> findAllByCategoryAndDeletedFalse(String category);
}