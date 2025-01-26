package hu.beni.tester.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimeTo {

    private long fullRun;

    private List<Long> createAmusementParksWithMachines;

    private List<Long> findAllParksPagedBeforeVisitorStuff;

    private List<Long> wholeVisitorStuff;

    private List<Long> tenParkVisitorStuff;

    private List<Long> oneParkVisitorStuff;

    private List<Long> findAllParksPagedAfterVisitorStuff;

    private List<Long> findAllVisitorsPaged;

    private DeleteTime deleteParks;

}