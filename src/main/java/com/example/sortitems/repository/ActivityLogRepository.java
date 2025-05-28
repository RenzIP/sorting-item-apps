package com.example.sortitems.repository;

import com.example.sortitems.model.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> { // Changed from Long to String
}