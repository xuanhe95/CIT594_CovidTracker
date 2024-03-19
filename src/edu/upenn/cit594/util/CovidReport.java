package edu.upenn.cit594.util;

import java.time.LocalDate;
/**
 * @author Xuanhe Zhang
 */
public record CovidReport(LocalDate reportTime, String zipCode, int partiallyVaccinated, int fullyVaccinated,
                         int boosterDoses) {


}
