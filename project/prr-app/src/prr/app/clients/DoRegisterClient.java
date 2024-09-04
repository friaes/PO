package prr.app.clients;

import prr.Network;
import prr.app.exceptions.DuplicateClientKeyException;
import prr.exceptions.ClientExistsException;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;


/**
 * Register new client.
 */
class DoRegisterClient extends Command<Network> {

	DoRegisterClient(Network receiver) {
		super(Label.REGISTER_CLIENT, receiver);
                //FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
        try {
			Form request = new Form();
			request.addStringField("key", Prompt.key());
			request.addStringField("name", Prompt.name());
			request.addStringField("TaxID", Prompt.taxId());
			request.parse();
			// null means get a new UUID
			_receiver.registerClient(new String[] { //
				"CLIENT", request.stringField("key"), //
				request.stringField("name"), request.stringField("TaxID") //
			});
		  } catch (ClientExistsException e) {
			throw new DuplicateClientKeyException(e.getKey());
		  } 
		}
	}


