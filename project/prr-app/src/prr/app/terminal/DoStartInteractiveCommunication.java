package prr.app.terminal;

import prr.Network;
import prr.app.exceptions.UnknownTerminalKeyException;
import prr.exceptions.TerminalExistsException;
import prr.exceptions.UnknownTerminalException;
import prr.terminals.Terminal;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add more imports if needed

/**
 * Command for starting communication.
 */
class DoStartInteractiveCommunication extends TerminalCommand {

	DoStartInteractiveCommunication(Network context, Terminal terminal) {
		super(Label.START_INTERACTIVE_COMMUNICATION, context, terminal, receiver -> receiver.canStartCommunication());
	}

	@Override
	protected final void execute() throws CommandException {
        try {
			String type;
			Form request = new Form();
			request.addStringField("key", Prompt.terminalKey());
			request.parse();
			String destinationKey = request.stringField("key");

			do {
				type = Form.requestString(Prompt.commType());
			}while (!type.equals("VIDEO") && !type.equals("VOICE"));

			if (type.equals("VIDEO")){
				if (_receiver.toString().equals("BASIC")){
					_display.popup(Message.unsupportedAtOrigin(_receiver.getKey(), type));
					return;
				}
				
				else if (_receiver.toString().equals("BASIC")){
					_display.popup(Message.unsupportedAtDestination(destinationKey, type));
					return;
				}
			}

			if(_receiver.getKey().equals(destinationKey)){
				 _display.popup(Message.destinationIsBusy(destinationKey));
				return;
			}

			switch (_receiver.commReceiverState(destinationKey, _network)){
				case "OFF" : _display.popup(Message.destinationIsOff(destinationKey));
							_receiver.addMissedCommToReceiver(new String[] {type,
								 request.stringField("key")}, _network);
							break;

				case "SILENCE" : _display.popup(Message.destinationIsSilent(destinationKey));
							_receiver.addMissedCommToReceiver(new String[] {type,
									request.stringField("key")}, _network);
								break;

				case "BUSY" : _display.popup(Message.destinationIsBusy(destinationKey));
								_receiver.addMissedCommToReceiver(new String[] {type,
									request.stringField("key")}, _network);
								break;

				case "IDLE" : _receiver.startInteractiveComm(new String[] { type,
								request.stringField("key") }, _network);
								break;
			}
		} catch (UnknownTerminalException e) {
			throw new UnknownTerminalKeyException(e.getKey());
		}
	}
	}
