package prr.app.clients;

import prr.Network;
import prr.app.exceptions.UnknownClientKeyException;
import prr.exceptions.NotificationsAlreadyOffException;
import prr.exceptions.UnknownClientException;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add more imports if needed

/**
 * Disable client notifications.
 */
class DoDisableClientNotifications extends Command<Network> {

	DoDisableClientNotifications(Network receiver) {
		super(Label.DISABLE_CLIENT_NOTIFICATIONS, receiver);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			Form request = new Form();
			request.addStringField("key", Prompt.key());
			request.parse();
			_receiver.disableClientNotifications(request.stringField("key"));
		} catch (UnknownClientException e) {
			throw new UnknownClientKeyException(e.getKey());
		} catch (NotificationsAlreadyOffException e){
			_display.popup(Message.clientNotificationsAlreadyDisabled());
		}
	}
}
