package com.avra.qa.secondmodulename.group.util.generator;

import com.avra.qa.common.util.datautil.RandomTestDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupDataGenerator {

    @Autowired
    private RandomTestDataGenerator generators;

    public UUID generateGroupId() {
        return generators.generateUuid();
    }

    public UUID generateCompanyId() {
        return generators.generateUuid();
    }
}
