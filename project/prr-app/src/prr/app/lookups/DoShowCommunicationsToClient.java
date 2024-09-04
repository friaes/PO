package prr.app.lookups;

import prr.Network;
import prr.exceptions.UnknownClientException;
import prr.app.visitors.RenderComms;
import prr.app.exceptions.UnknownClientKeyException;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add more imports if needed

/**
 * Show communications to a client.
 */
class DoShowCommunicationsToClient extends Command<Network> {

	DoShowCommunicationsToClient(Network receiver) {
		super(Label.SHOW_COMMUNICATIONS_TO_CLIENT, receiver);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		try{
			Form request = new Form();
			request.addStringField("key", Prompt.clientKey());
			request.parse();
			RenderComms renderer = new RenderComms();
			_receiver.acceptCommsToClient(renderer, request.stringField("key"));
			if (renderer.toString().length() != 0)
				_display.popup(renderer);
		
		} catch (UnknownClientException e){
			throw new UnknownClientKeyException(e.getKey());
		}
	}
}
