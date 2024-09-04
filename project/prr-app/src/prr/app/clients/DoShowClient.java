package prr.app.clients;

import prr.Network;
import prr.app.visitors.RenderClients;
import prr.app.exceptions.UnknownClientKeyException;
import prr.exceptions.UnknownClientException;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show specific client: also show previous notifications.
 */
class DoShowClient extends Command<Network> {

	DoShowClient(Network receiver) {
		super(Label.SHOW_CLIENT, receiver);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		try{	
			Form request = new Form();
			request.addStringField("key", Prompt.key());
			request.parse();
			RenderClients renderer = new RenderClients();
			String notifications;
			_receiver.acceptClient(renderer, request.stringField("key"));
			notifications = _receiver.showNotificationsClient(request.stringField("key"));
			if (renderer.toString().length() != 0)
				_display.popup(renderer);
			if(notifications.length() != 0)
				_display.popup(notifications);

		} catch (UnknownClientException e){
			throw new UnknownClientKeyException(e.getKey());
		}

	}
}

