package com.example.sortitems.repository;

import com.example.sortitems.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, ObjectId> {
    List<Item> findAllByOrderByNameAsc();
    List<Item> findAllByOrderByQuantityAsc();
}