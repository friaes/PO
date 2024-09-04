package prr.app.clients;

import prr.Network;
import prr.app.exceptions.UnknownClientKeyException;
import prr.exceptions.NotificationsAlreadyOnException;
import prr.exceptions.UnknownClientException;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add more imports if needed

/**
 * Enable client notifications.
 */
class DoEnableClientNotifications extends Command<Network> {

	DoEnableClientNotifications(Network receiver) {
		super(Label.ENABLE_CLIENT_NOTIFICATIONS, receiver);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
        try {
			Form request = new Form();
			request.addStringField("key", Prompt.key());
			request.parse();
			_receiver.enableClientNotifications(request.stringField("key"));
		} catch (UnknownClientException e) {
			throw new UnknownClientKeyException(e.getKey());
		} catch (NotificationsAlreadyOnException e){
			_display.popup(Message.clientNotificationsAlreadyEnabled());
		}
	}
}
