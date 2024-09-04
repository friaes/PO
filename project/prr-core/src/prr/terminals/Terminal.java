package prr.terminals;

import prr.Network;
import prr.clients.Client;
import prr.communications.*;
import prr.notifications.Notification;
import prr.exceptions.TerminalExistsException;
import prr.exceptions.UnknownTerminalException;
import prr.visits.TerminalVisitor;
import prr.visits.CommVisitor;
import prr.visits.Selector;

import java.util.Map;
import java.util.TreeMap;

import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

// FIXME add more import if needed (cannot import from pt.tecnico or prr.app)

/**
 * Abstract terminal.
 */
public abstract class Terminal implements Serializable /* FIXME maybe addd more interfaces */ {

        /** Serial number for serialization. */
        private static final long serialVersionUID = 202208091753L;

        private String _key;
        private Client _client;
        private Map<String, Terminal> _friends = new TreeMap<String, Terminal>();
        private TreeMap<Long, Communication> _comms = new TreeMap<Long, Communication>();
        private List<Communication> _missedComms = new ArrayList<Communication>();
        private State _state;
        private State _prevState;
        private long _debts = 0;
        private long _payments = 0;

        public Terminal(String key, Client client, String state) {
                _key = key;
                _client = client;
                if (state.equals("ON"))
                        _state = new Idle(this);
                if (state.equals("OFF"))
                        _state = new Off(this);
                if (state.equals("SILENCE"))
                        _state = new Silent(this);
        }

        public abstract void accept(TerminalVisitor visitor, boolean last);

/*-------------------------------------State Class----------------------------------------*/

        public abstract class State implements Serializable {

                public abstract void toIdle(Terminal terminal);

                public abstract void toSilent(Terminal terminal);

                public abstract void toOff(Terminal terminal);

                public abstract void toBusy(Terminal terminal);

                protected void setState(State state) {
                        _state = state;
                }
        }
        
/*----------------------------------Getters & Setters-------------------------------------*/

        public State getState() {
                return _state;
        }

        public String getKey() {
                return _key;
        }

        public String getClientKey() {
                return _client.getKey();
        }

        public String getStateString() {
                return _state.toString();
        }

        public long getDebts() {
                return _debts;
        }

        public long getPayments() {
                return _payments;
        }

        public long getLastDebt() {
                return getLastComm().getPrice();
        }

        public Map<String, Terminal> getFriends() {
                return _friends;
        }

        public Map<Long, Communication> getComms() {
                return _comms;
        }

        public long getBalance() {
                return _payments - _debts;
        }

        public State getPrevState() {
                return _prevState;
        }

        public Communication getLastComm() {
                if (_comms.isEmpty())
                        return null;
                return _comms.get(_comms.lastKey());
        }

        public void addDebt(long debt) {
                _debts += debt;
        }

        public void addPayment(long payment) {
                _payments += payment;
        }

        public void removeDebt(long debt) {
                _debts -= debt;
        }

        public void setPrevState(State state) {
                _prevState = state;
        }


/*----------------------------------State Transitions-----------------------------------*/



        public void toIdle(Network network) {
                if (!_missedComms.isEmpty()) {
                        for (Communication communication : _missedComms) {
                                Terminal sender = communication.getSender();
                                switch (getStateString()) {
                                        case "OFF" -> sender.sendNotification(
                                                new Notification("O2I", getKey()));
                                        case "SILENCE" -> sender.sendNotification(
                                                new Notification("S2I", getKey()));
                                        case "BUSY" -> sender.sendNotification(
                                                new Notification("B2I", getKey()));
                                }
                        }
                }

                _missedComms.clear();
                _state.toIdle(this);
                network.changed();

        }

        public void toBusy() {
                _state.toBusy(this);
        }

        public void toOff(Network network) {
                _state.toOff(this);
                network.changed();
        }

        public void toSilent(Network network) {
                if (!_missedComms.isEmpty()) {
                        for (Communication communication : _missedComms) {
                                Terminal sender = communication.getSender();
                                switch (getStateString()) {
                                        case "OFF" -> sender.sendNotification(new Notification("O2S", getKey()));
                                }

                        }
                }

                _missedComms.clear();
                _state.toSilent(this);
                network.changed();

        }

        public void toOriginState(Network network) {
                switch (_prevState.toString()) {
                        case "IDLE" -> toIdle(network);
                        case "SILENCE" -> toSilent(network);
                }

        }

/*----------------------------------Payments & Debts--------------------------------------*/

        public boolean canPerformPayment(long key) {
                Communication comm = _comms.get(key);
                if (comm == null)
                        return false;
                else if (comm.paidComm() || !isSender(comm) || comm.isOnGoing())
                        return false;

                return true;
        }

        public void performPayment(long key, Network network) {
                Communication comm = _comms.get(key);
                comm.payComm();
                long price = comm.getPrice();
                addPayment(price);
                removeDebt(price);

                _client.addPayment(price);
                _client.removeDebt(price);
                _client.changeLevel();

                network.addPayment(price);
                network.removeDebt(price);
                network.changed();
        }

/*---------------------------------Communication Related---------------------------------*/

        public String commReceiverState(String receiverKey, Network network) throws UnknownTerminalException {
                return network.getTerminalStateString(receiverKey);
        }

        /**
         * Checks if this terminal can end the current interactive communication.
         *
         * @return true if this terminal is busy (i.e., it has an active interactive
         *         communication) and
         *         it was the originator of this communication.
         **/
        public boolean canEndCurrentCommunication() {
                Communication last = getLastComm();
                if (last == null)
                        return false;
                if (_state instanceof Busy && isSender(last))
                        return true;
                return false;
        }

        /**
         * Checks if this terminal can start a new communication.
         *
         * @return true if this terminal is neither off neither busy, false otherwise.
         **/
        public boolean canStartCommunication() {
                if (_state instanceof Off || _state instanceof Busy)
                        return false;
                return true;
        }


        public boolean isSender(Communication comm) {
                Terminal sender = comm.getSender();
                return equals(sender);
        }


        public void addCommunication(Communication comm) {
                _comms.put(comm.getKey(), comm);
                _client.addCommunication(comm);

        }


        public void textComm(Terminal receiver, String textMessage, long commKey, Network network) {
                Communication comm = new Text(this, receiver, textMessage, commKey);
                addCommunication(comm);
                receiver.addCommunication(comm);
                network.addCommunication(comm);
                comm.endComm();

                long price = _client.calculateTextPrice(comm.units());
                if (_friends.containsKey(comm.getReceiver().getKey()))
                        price /= 2;

                comm.setPrice(price);
                addDebt(price);
                _client.addDebt(price);
                network.changed();
        }


        public void sendTextCommunication(String[] fields, Network network) throws UnknownTerminalException {
                network.checkTerminalExists(fields[0]);
                Terminal receiver = network.getTerminal(fields[0]);
                textComm(receiver, fields[1], network.nextCommKey(), network);
                network.addDebt(getLastDebt());
                _client.incrementCommsAfterLevelChange();
                _client.changeLevel();
                network.changed();
        }


        public void videoComm(Terminal receiver, long commKey, Network network) {
                Communication comm = new Video(this, receiver, commKey);
                addCommunication(comm);
                receiver.addCommunication(comm);
                network.addCommunication(comm);
        }

        public void voiceComm(Terminal receiver, long commKey, Network network) {
                Communication comm = new Voice(this, receiver, commKey);
                addCommunication(comm);
                receiver.addCommunication(comm);
                network.addCommunication(comm);
        }


        public void startInteractiveComm(String[] fields, Network network) throws UnknownTerminalException {
                network.checkTerminalExists(fields[1]);
                Terminal receiver = network.getTerminal(fields[1]);
                if (fields[0].equals("VIDEO"))
                        videoComm(receiver, network.nextCommKey(), network);
                else
                        voiceComm(receiver, network.nextCommKey(), network);
                setPrevState(_state);
                toBusy();
                receiver.setPrevState(receiver.getState());
                receiver.toBusy();
                network.changed();
        }


        public void endCurrentCommunication(long duration, Network network) {
                Communication last = getLastComm();
                last.setUnits(duration);
                last.endComm();
                toOriginState(network);
                last.getReceiver().toOriginState(network);

                long price;
                if (last instanceof Video)
                        price = _client.calculateVideoPrice(last.units());
                else
                        price = _client.calculateVoicePrice(last.units());

                if (_friends.containsKey(last.getReceiver().getKey()))
                        price /= 2;
                last.setPrice(price);
                addDebt(price);
                _client.addDebt(price);
                _client.incrementCommsAfterLevelChange();
                _client.changeLevel();
                network.addDebt(price);
                network.changed();
        }

/*----------------------------------Notifications Related---------------------------------*/

        public void addMissedComm(Communication communication) {
                _missedComms.add(communication);
        }

        public void sendNotification(Notification notification) {
                _client.addNotification(notification);
        }

        public void addMissedCommToReceiver(String[] fields, Network network) throws UnknownTerminalException {
                network.checkTerminalExists(fields[1]);
                Terminal receiver = network.getTerminal(fields[1]);
                Communication missedComm;
                if (fields[0].equals("VIDEO"))
                        missedComm = new Video(this, receiver, 0);
                else if (fields[1].equals("VOICE"))
                        missedComm = new Voice(this, receiver, 0);
                else
                        missedComm = new Text(this, receiver, "", 0);

                receiver.addMissedComm(missedComm);
                network.changed();
        }

/*-----------------------------Comms visitor accept method--------------------------------*/
/*                             (for ongoing communications)                               */

        public void acceptComms(Selector<Communication> selector, CommVisitor visitor) {
                long commsCount = 0;
                List<Communication> onGoingComms = getOngoingComms();
                long numberOfComms = onGoingComms.size();
                for (Communication communication : onGoingComms) {
                        ++commsCount;
                        if (selector.ok(communication))
                                communication.accept(visitor, commsCount == numberOfComms);
                }
        }

        public void acceptComms(CommVisitor visitor) {
                acceptComms(new Selector<Communication>() {
                }, visitor);
        }

        public List<Communication> getOngoingComms() {
                List<Communication> onGoingComms = new ArrayList<Communication>();
                for (Communication communication : _comms.values())
                        if (communication.getStatus())
                                onGoingComms.add(communication);
                return onGoingComms;
        }


/*----------------------------------Friends----------------------------------------------*/

        public void addFriend(Terminal terminal) {
                _friends.put(terminal.getKey(), terminal);
        }

        public void addFriend(String key, Network network) throws TerminalExistsException, UnknownTerminalException {
                if (!_friends.containsKey(key) && !key.equals(_key)) {
                        network.checkTerminalExists(key);
                        addFriend(network.getTerminal(key));
                        network.changed();
                }
        }

        public void removeFriend(Terminal terminal) {
                _friends.remove(terminal.getKey());
        }

        public void removeFriend(String key, Network network) throws TerminalExistsException, UnknownTerminalException {
                if (_friends.containsKey(key)) {
                        network.checkTerminalExists(key);
                        removeFriend(network.getTerminal(key));
                        network.changed();
                }
        }


        public String toStringFriends() {
                String stringFriends = "";
                if (_friends.size() != 0) {
                        stringFriends = "|";
                        int contador = _friends.size();
                        for (String k : _friends.keySet()) {
                                stringFriends += k;
                                if (contador > 1)
                                        stringFriends += ",";
                                contador--;
                        }
                }
                return stringFriends;
        }

}
