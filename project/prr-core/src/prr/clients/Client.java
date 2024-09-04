package prr.clients;

import prr.tariffplan.BasicTariff;
import prr.communications.Communication;
import prr.terminals.Terminal;
import prr.visits.ClientVisitor;
import prr.notifications.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.Serializable;

public class Client implements Serializable {

    private static final long serialVersionUID = 202208091753L;

    private String _name;
    private String _key;
    private long _taxID;
    private Map<String, Terminal> _terminals = new TreeMap<String, Terminal>();
    private Level _level;
    private List<Notification> _notifications = new ArrayList<Notification>();
    private List<Communication> _comms = new ArrayList<Communication>();
    private long _commsAfterLevelChange = 0;

    private boolean notificationsON = true;
    private long _payments = 0;
    private long _debts = 0;

    public Client(String key, String name, long taxID) {
        _key = key;
        _name = name;
        _taxID = taxID;
        _level = new Normal(this);
    }

    public void accept(ClientVisitor visitor, boolean last) {
        visitor.visitClient(this, last);
    }

/*-------------------------------------Level Class----------------------------------------*/

    public abstract class Level implements Serializable {

        private BasicTariff _tariff;

        public Level(BasicTariff tariff) {
            _tariff = tariff;
        }

        public abstract void resetLevel(Client client);

        public abstract void downGrade(Client client);

        public abstract void upGrade(Client client);

        public abstract String toString();

        public BasicTariff getTariff() {
            return _tariff;
        }

        protected void setLevel(Level level) {
            Client.this._level = level;
        }
    }

/*----------------------------------Getters & Setters-------------------------------------*/

    public String getKey() {
        return _key;
    }

    public String getLevel() {
        return _level.toString();
    }

    public String getName() {
        return _name;
    }

    public long getTaxID() {
        return _taxID;
    }

    public long getPayments() {
        return _payments;
    }

    public long getDebts() {
        return _debts;
    }

    public long getBalance() {
        return _payments - _debts;
    }

    public int getTerminalsSize() {
        return _terminals.size();
    }

    public Map<Long, Communication> getCommsFromThis() {
        Map<Long, Communication> commsFromThis = new TreeMap<Long, Communication>();
        for (Communication comm : _comms) {
            if (_terminals.containsKey(comm.getSenderKey()))
                commsFromThis.put(comm.getKey(), comm);
        }
        return commsFromThis;
    }

    public Map<Long, Communication> getCommsToThis() {
        Map<Long, Communication> commsToThis = new TreeMap<Long, Communication>();
        for (Communication comm : _comms) {
            if (_terminals.containsKey(comm.getReceiverKey()))
                commsToThis.put(comm.getKey(), comm);
        }
        return commsToThis;
    }

/*--------------------------------Terminal Related--------------------------------------*/

    public void addTerminal(Terminal terminal) {
        String key = terminal.getKey();
        _terminals.put(key, terminal);
    }

/*------------------------------Communication Related------------------------------------*/

    public void addCommunication(Communication communication) {
        _comms.add(communication);
    }

/*---------------------------------Payments & Debts--------------------------------------*/

    public long calculateTextPrice(long units) {
        return _level.getTariff().calculateText(units);
    }

    public long calculateVideoPrice(long units) {
        return _level.getTariff().calculateVideo(units);
    }

    public long calculateVoicePrice(long units) {
        return _level.getTariff().calculateVoice(units);
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

    public static Comparator<Client> debtComparator = new Comparator<Client>() {

        @Override
        public int compare(Client c1, Client c2) {
            if (c1.getDebts() == c2.getDebts())
                return 0;
            else if (c1.getDebts() > c2.getDebts())
                return -1;
            else
                return 1;
        }
    };

/*------------------------------Notification Related-------------------------------------*/

    public void setNotifications(boolean n) {
        notificationsON = n;
    }

    public String NotificationsToString() {
        if (notificationsON)
            return "YES";
        return "NO";
    }

    public void addNotification(Notification newNotification) {
        for (Notification notification : _notifications)
            if (notification.getTerminalKey().equals(newNotification.getTerminalKey()))
                return;
        _notifications.add(newNotification);
    }

    public String sendNotifications(NotificationStrategy strategy) {
        String render = "";
        long notifCount = _notifications.size();
        if (notifCount != 0) {
            for (int i = 0; i < notifCount; i++) {
                if (i == 0)
                    render += strategy.sendNotification(_notifications.get(i));

                else
                    render += "\n" + strategy.sendNotification(_notifications.get(i));
            }
            _notifications.clear();
        }
        return render;
    }

/*-----------------------------------Level Transitions----------------------------------- */

    public void incrementCommsAfterLevelChange() {
        _commsAfterLevelChange++;

    }

    public boolean last5VideoComms() {
        int lastIndex = _comms.size() - 1;

        if (lastIndex < 4)
            return false;

        for (int i = lastIndex - 4; i <= lastIndex; i++) {
            if (!_comms.get(i).toString().equals("VIDEO"))
                return false;
        }
        return true;
    }

    public boolean last2TextComms() {
        int lastIndex = _comms.size() - 1;

        if (lastIndex < 1)
            return false;

        for (int i = lastIndex - 1; i <= lastIndex; i++) {
            if (!_comms.get(i).toString().equals("TEXT"))
                return false;
        }
        return true;
    }

    public void changeLevel() {
        switch (_level.toString()) {
            case "NORMAL":
                if (this.getBalance() > 500) {
                    _level.upGrade(this);
                    _commsAfterLevelChange = 0;
                }
                break;

            case "GOLD":
                if (this.getBalance() >= 0 && last5VideoComms() && _commsAfterLevelChange >= 5) {
                    _level.upGrade(this);
                    _commsAfterLevelChange = 0;
                } else if (this.getBalance() < 0)
                    _level.resetLevel(this);
                break;

            case "PLATINUM":
                if (this.getBalance() >= 0 && last2TextComms() && _commsAfterLevelChange >= 2) {
                    _commsAfterLevelChange = 0;
                    _level.downGrade(this);
                } else if (this.getBalance() < 0)
                    _level.resetLevel(this);
                break;
        }

    }

}
