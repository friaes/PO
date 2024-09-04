package prr.app.lookups;

import prr.Network;
import prr.exceptions.UnknownClientException;
import prr.app.visitors.RenderComms;
import prr.app.exceptions.UnknownClientKeyException;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show communications from a client.
 */
class DoShowCommunicationsFromClient extends Command<Network> {

	DoShowCommunicationsFromClient(Network receiver) {
		super(Label.SHOW_COMMUNICATIONS_FROM_CLIENT, receiver);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		try{
			Form request = new Form();
			request.addStringField("key", Prompt.clientKey());
			request.parse();
			RenderComms renderer = new RenderComms();
			_receiver.acceptCommsFromClient(renderer, request.stringField("key"));
			if (renderer.toString().length() != 0)
				_display.popup(renderer);
		
		} catch (UnknownClientException e){
			throw new UnknownClientKeyException(e.getKey());
		}
	}
}
