package com.example.sortitems.service;

import com.example.sortitems.model.ActivityLog;
import com.example.sortitems.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logActivity(String action, String itemId, String itemName) {
        ActivityLog log = new ActivityLog(action, itemId, itemName);
        activityLogRepository.save(log);
    }
}