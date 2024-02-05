package pl.hofman.projectsGmailApi.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum MonthEnum {

    JANUARY("01", "January"),
    FEBRUARY("02", "February"),
    MARCH("03", "March"),
    APRIL("04", "April"),
    MAY("05", "May"),
    JUNE("06", "June"),
    JULY("07", "July"),
    AUGUST("08", "August"),
    SEPTEMBER("09", "September"),
    OCTOBER("10", "October"),
    NOVEMBER("11", "November"),
    DECEMBER("12", "December");

    private String code;
    private String name;

    private MonthEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, MonthEnum> monthMap;

    static {
        monthMap = new HashMap<>();
        Arrays.stream(MonthEnum.values())
                .forEach(monthEnum -> monthMap.putIfAbsent(monthEnum.code, monthEnum));
    }

    public static MonthEnum getMonthByNumber(String code) {
        return monthMap.get(code);
    }

}
