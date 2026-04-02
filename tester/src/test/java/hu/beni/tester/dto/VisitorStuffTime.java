package hu.beni.tester.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class VisitorStuffTime {

    private final long wholeTime;

    private final List<Long> tenParkTimes;

    private final List<Long> oneParkTimes;

}
