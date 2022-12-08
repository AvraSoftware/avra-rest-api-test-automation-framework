package com.avra.qa.secondmodulename.group.util.rabbitmq;

import com.avra.qa.common.util.datautil.FileContentReader;
import com.avra.qa.common.util.datautil.queue.RabbitConnectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GroupSenderService {

    private static final String EVENT_TYPE_CREATED = "created";
    private static final String EVENT_TYPE_UPDATED = "updated";
    private static final String GROUP = "group";
    private static final String QUEUE_NAME = "system-name.queue.created";
    private static final String SECOND_QUEUE_NAME = "system-name.second-queue.updated";
    private static final String GROUP_RESOURCE_PATH = "secondmodulename/group/queue/";

    @Autowired
    private RabbitConnectionHelper rabbitConnectionHelper;

    @Autowired
    private FileContentReader fileContentReader;

    public void sendGroup(String fileName, UUID generatedId, UUID generatedCompanyId) throws Exception {

        String payload = fileContentReader.getFileContent(GROUP_RESOURCE_PATH + fileName);

        String payloadTemplate = payload.replaceAll("!\\{generatedId\\}", generatedId.toString());
        payloadTemplate = payloadTemplate.replaceAll("!\\{generatedCompanyId\\}", generatedCompanyId.toString());

        rabbitConnectionHelper.publishSystemNameEvent(EVENT_TYPE_CREATED, GROUP, QUEUE_NAME, payloadTemplate);
    }

    public void sendGroup(String fileName, UUID generatedId, String name, UUID generatedCompanyId) throws Exception {

        String payload = fileContentReader.getFileContent(GROUP_RESOURCE_PATH + fileName);

        String payloadTemplate = payload.replaceAll("!\\{generatedId\\}", generatedId.toString());
        payloadTemplate = payloadTemplate.replaceAll("!\\{generatedCompanyId\\}", generatedCompanyId.toString());
        payloadTemplate = payloadTemplate.replaceAll("!\\{name\\}", name);

        rabbitConnectionHelper.publishSystemNameEvent(EVENT_TYPE_CREATED, GROUP, QUEUE_NAME, payloadTemplate);
    }

    public void updateGroup(String fileName, UUID generatedId, UUID generatedCompanyId) throws Exception {

        String payload = fileContentReader.getFileContent(GROUP_RESOURCE_PATH + fileName);

        String payloadTemplate = payload.replaceAll("!\\{generatedId\\}", generatedId.toString());
        payloadTemplate = payloadTemplate.replaceAll("!\\{generatedCompanyId\\}", generatedCompanyId.toString());

        rabbitConnectionHelper.publishSystemNameEvent(EVENT_TYPE_UPDATED, GROUP, SECOND_QUEUE_NAME, payloadTemplate);
    }
}
