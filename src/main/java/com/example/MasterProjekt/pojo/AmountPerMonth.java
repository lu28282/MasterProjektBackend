package com.example.MasterProjekt.pojo;

import java.time.YearMonth;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AmountPerMonth implements Comparable<AmountPerMonth> {

    YearMonth yearMonth;

    Long amount;

    @Override
    public int compareTo(AmountPerMonth a) {
        return yearMonth.compareTo(a.getYearMonth());
    }
}
