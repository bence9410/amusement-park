package hu.beni.tester.output;

import hu.beni.tester.dto.TimeTo;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.properties.NumberOfProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import static hu.beni.tester.constants.Constants.SEMICOLON;

@Slf4j
public class ResultLogger {

    private static final String NEW_LINE = "\n";

    private final String[] header = {"", "fullRun", "wholeVisitorStuff", "tenParkVisitorStuff", "oneParkVisitorStuff"};
    private final String[] result;

    public ResultLogger(TimeTo timeTo, ApplicationProperties properties) {
        NumberOfProperties numberOf = properties.getNumberOf();
        result = new String[]{
                numberOf.getAdmins() + "a " + numberOf.getVisitors() + "v " + numberOf.getAmusementParksPerAdmin()
                        + "p/a " + numberOf.getMachinesPerPark() + "m/p ",
                Long.toString(timeTo.getFullRun()),
                minAvgMax(timeTo.getWholeVisitorStuff()),
                minAvgMax(timeTo.getTenParkVisitorStuff()),
                minAvgMax(timeTo.getOneParkVisitorStuff())};
    }

    public void logToConsole() {
        StringBuilder sb = new StringBuilder(NEW_LINE);
        for (int i = 0; i < header.length; i++) {
            if (i != 0) {
                sb.append(header[i]).append(": ");
            }
            sb.append(result[i]);
            if (i != header.length - 1) {
                sb.append(NEW_LINE);
            }
        }
        log.info(sb.toString());
    }

    private String minAvgMax(List<Long> list) {
        long first = list.getFirst();
        int size = list.size();
        long min = first;
        long avg = first;
        long max = first;
        for (int i = 1; i < list.size(); i++) {
            long act = list.get(i);
            if (act < min) {
                min = act;
            }
            if (act > max) {
                max = act;
            }
            avg += act;
        }
        avg /= size;
        return min + ", " + avg + ", " + max;
    }
}
