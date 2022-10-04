package tu.LimitSwitch;

/**
 * Created by user on 26.10.16.
 */
public enum LimitSwitch {
    BSKP_1("БСКП №1", 221, "192.168.111.231", "230.0.221.1",43501,1001,"230.0.221.10",43510, 1010,1),
    BSKP_2("БСКП №2", 222, "192.168.121.232", "230.0.222.1",43601,1001,"230.0.222.10",43610, 1010,2),
    BSKP_3("БСКП №3", 223, "192.168.111.233", "230.0.223.1",43701,1001,"230.0.223.10",43710, 1010,3),
    BSKP_4("БСКП №4", 224, "192.168.121.234", "230.0.224.1",43801,1001,"230.0.224.10",43810, 1010,4);

 /*   BOI_E( "БСКП №2", 222, "230.0.222.1",43601,1001,"230.0.222.2",43910, 1010),
    BOI_K( "БСКП №2", 222, "230.0.222.1",43601,1001,"230.0.222.2",43610, 1010);*/

    private final String name;
    private final Integer id;
    private final String address;

    private final String kssGroup;
    private final Integer kssPort;
    private final Integer kssId;

    private final String alarmGroup;
    private final Integer alarmPort;
    private final Integer alarmId;
    private final Integer alarmData;

    LimitSwitch(String name,
                Integer id,
                String address,
                String kssGroup,
                Integer kssPort,
                Integer kssId,
                String alarmGroup,
                Integer alarmPort,
                Integer alarmId, Integer alarmData){
        this.name = name;
        this.id = id;
        this.address = address;
        this.kssGroup = kssGroup;
        this.kssPort = kssPort;
        this.kssId = kssId;
        this.alarmGroup = alarmGroup;
        this.alarmPort = alarmPort;
        this.alarmId = alarmId;
        this.alarmData = alarmData;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getKssGroup() {
        return kssGroup;
    }

    public Integer getKssPort() {
        return kssPort;
    }

    public Integer getKssId() {
        return kssId;
    }

    public String getAlarmGroup() {
        return alarmGroup;
    }

    public Integer getAlarmPort() {
        return alarmPort;
    }

    public Integer getAlarmId() {
        return alarmId;
    }

    public String getAddress() {
        return address;
    }

    public Integer getAlarmData() {
        return alarmData;
    }

    @Override
    public String toString() {
        return name;
    }
}
