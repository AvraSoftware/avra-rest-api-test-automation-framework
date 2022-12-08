package com.avra.qa.secondmodulename.group.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupAssert {

    public static void assertReceiveGroup(Map<String, String> groupData,
                                          UUID expectedId,
                                          UUID expectedCompanyId,
                                          String expectedActiveStatus,
                                          String expectedName) {

        assertEquals(groupData.size(), 4);
        assertEquals(groupData.get("id"), expectedId.toString());
        assertEquals(groupData.get("company_id"), expectedCompanyId.toString());
        assertEquals(groupData.get("active"), expectedActiveStatus);
        assertEquals(groupData.get("name"), expectedName);
    }

    public static void assertReceiveAllGroups(List<Map<String, Object>> groupData, UUID id, UUID companyId, String active, String name) {
        assertEquals(groupData.size(), 2);

        assertEquals(groupData.get(0).get("id"), id.toString());
        assertEquals(groupData.get(0).get("company_id"), companyId);
        assertEquals(groupData.get(0).get("active"), active);
        assertEquals(groupData.get(0).get("name"), name);

        assertEquals(groupData.get(1).get("id"), id.toString());
        assertEquals(groupData.get(1).get("company_id"), companyId);
        assertEquals(groupData.get(1).get("active"), active);
        assertEquals(groupData.get(1).get("name"), name);
    }
}
