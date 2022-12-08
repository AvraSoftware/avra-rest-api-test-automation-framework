package com.avra.qa.secondmodulename.group.util.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class GroupDatabaseService {

    @Autowired
    private GroupRepository groupRepository;

    public Map<String, String> getGroup(UUID id) {
        try {
            return groupRepository.getGroup(id);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("No database row was found for group with id: " + id);
        }
    }

    public void deleteAllGroups() {
        groupRepository.deleteAllGroups();
    }
}
