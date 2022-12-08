package com.avra.qa.common.testdata.group;

import com.avra.qa.secondmodulename.group.util.generator.GroupDataGenerator;
import com.avra.qa.secondmodulename.group.util.jpa.GroupDatabaseService;
import com.avra.qa.secondmodulename.group.util.rabbitmq.GroupSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Component that aggregates beans from a given domain, designed to be shared between modules and domains.
 * Reduces the number of dependencies in test classes
 */

@Component
@RequiredArgsConstructor
public class GroupTestData {

    private static final String GROUP_FILE_NAME = "GroupWithUniqueName.json";

    private final GroupDataGenerator groupDataGenerator;
    private final GroupSenderService groupSenderService;
    private final GroupDatabaseService groupDatabaseService;

    public UUID sendGroupWithGivenName(UUID companyId, String name) throws Exception {
        UUID groupId = groupDataGenerator.generateGroupId();
        groupSenderService.sendGroup(GROUP_FILE_NAME, groupId, name, companyId);
        return groupId;
    }

    public void sendGroup(String fileName, UUID groupId, UUID companyId) throws Exception {
        groupSenderService.sendGroup(fileName, groupId, companyId);
    }

    public void deleteAllGroups() {
        groupDatabaseService.deleteAllGroups();
    }

    public UUID generateGroupId() {
        return groupDataGenerator.generateGroupId();
    }

    public UUID generateCompanyId() {
        return groupDataGenerator.generateCompanyId();
    }
}
