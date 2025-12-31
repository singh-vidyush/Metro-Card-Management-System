package org.vidyush.strategy;

public class WeekdayFareStrategy implements FareStrategy {

    @Override
    public double getFarePerStation() {
        return 7;
    }
}
