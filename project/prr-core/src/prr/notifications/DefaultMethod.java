package prr.notifications;

public class DefaultMethod implements NotificationStrategy{

    public String sendNotification(Notification notification){
        return notification.getMessage() + "|" + notification.getTerminalKey();
    }
    
}
