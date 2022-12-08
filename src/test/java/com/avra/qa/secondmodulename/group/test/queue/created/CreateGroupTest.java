package com.avra.qa.secondmodulename.group.test.queue.created;

import com.avra.qa.common.util.configuration.AppConfig;
import com.avra.qa.secondmodulename.group.util.generator.GroupDataGenerator;
import com.avra.qa.secondmodulename.group.util.jpa.GroupDatabaseService;
import com.avra.qa.secondmodulename.group.util.rabbitmq.GroupSenderService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;

import static com.avra.qa.secondmodulename.group.assertion.GroupAssert.assertReceiveGroup;
import static org.testng.Assert.assertThrows;

@Test(alwaysRun = true, groups = {"allTests", "externalData", "receivedFromSystemName"}, singleThreaded = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ContextConfiguration(classes = {AppConfig.class})
public class CreateGroupTest extends AbstractTestNGSpringContextTests {

    private static final String ACTIVE = "true";
    private static final String NAME = "Nazwa grupy";

    @Autowired
    private GroupSenderService groupSenderService;
    @Autowired
    private GroupDatabaseService groupDatabaseService;
    @Autowired
    private GroupDataGenerator groupDataGenerator;

    @AfterClass
    public void tearDown() {
        groupDatabaseService.deleteAllGroups();
    }

    @SneakyThrows
    @Test
    public void should_create_new_group() {
        //given
        UUID generatedId = groupDataGenerator.generateGroupId();
        UUID generatedCompanyId = groupDataGenerator.generateCompanyId();

        //when
        groupSenderService.sendGroup("Group.json", generatedId, generatedCompanyId);

        //then
        Map<String, String> groupData = groupDatabaseService.getGroup(generatedId);

        assertReceiveGroup(groupData, generatedId, generatedCompanyId, ACTIVE, NAME);
    }

    @SneakyThrows
    @Test
    public void should_not_create_group_with_all_empty_fields_besides_id() {
        //given
        UUID generatedId = groupDataGenerator.generateGroupId();
        UUID generatedCompanyId = groupDataGenerator.generateCompanyId();

        //when
        groupSenderService.sendGroup("GroupAllEmptyFieldsNotValid.json", generatedId, generatedCompanyId);

        //then
        assertThrows(RuntimeException.class, () -> groupDatabaseService.getGroup(generatedId));
    }

    @SneakyThrows
    @Test
    public void should_not_create_existing_group() {
        //given
        UUID generatedId = groupDataGenerator.generateGroupId();
        UUID generatedCompanyId = groupDataGenerator.generateCompanyId();

        //when
        groupSenderService.sendGroup("Group.json", generatedId, generatedCompanyId);
        groupSenderService.sendGroup("GroupExisting.json", generatedId, generatedCompanyId);

        //then
        Map<String, String> groupData = groupDatabaseService.getGroup(generatedId);

        assertReceiveGroup(groupData, generatedId, generatedCompanyId, ACTIVE, NAME);
    }

    @SneakyThrows
    @Test
    public void should_not_create_group_with_256_string_for_name() {
        //given
        UUID generatedId = groupDataGenerator.generateGroupId();
        UUID generatedCompanyId = groupDataGenerator.generateCompanyId();

        //when
        groupSenderService.sendGroup("GroupWith256StringForNameNotValid.json", generatedId, generatedCompanyId);

        //then
        assertThrows(RuntimeException.class, () -> groupDatabaseService.getGroup(generatedId));
    }

    @SneakyThrows
    @Test
    public void should_not_create_group_with_additional_field() {
        //given
        UUID generatedId = groupDataGenerator.generateGroupId();
        UUID generatedCompanyId = groupDataGenerator.generateCompanyId();

        //when
        groupSenderService.sendGroup("GroupWithAdditionalFieldNotValid.json", generatedId, generatedCompanyId);

        //then
        Map<String, String> groupData = groupDatabaseService.getGroup(generatedId);

        assertReceiveGroup(groupData, generatedId, generatedCompanyId, ACTIVE, "Nazwa pierwszej grupy zmodyfikowana");
    }
}
