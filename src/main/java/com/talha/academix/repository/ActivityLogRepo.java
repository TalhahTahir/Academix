package com.talha.academix.repository;

import com.talha.academix.enums.ActivityAction;
import com.talha.academix.model.ActivityLog;
import com.talha.academix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepo extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUser(User user);
    List<ActivityLog> findByAction(ActivityAction action);
}
