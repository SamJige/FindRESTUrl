package org.jige.service;

import com.intellij.openapi.project.Project;
import org.slf4j.LoggerFactory;

public class MyNotifier {
    //    private final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("Find REST Url logs", NotificationDisplayType.BALLOON, true);
//
    public void notify(Project project, String content) {
////        final Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION);
////        notification.notify(project);
        LoggerFactory.getLogger(getClass()).info("just simple log -> {}", content);
    }
}