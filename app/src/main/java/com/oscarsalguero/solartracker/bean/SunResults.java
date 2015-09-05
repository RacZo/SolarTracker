/***
 * Copyright (c) 2015 Oscar Salguero www.oscarsalguero.com
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oscarsalguero.solartracker.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Java POJO for Sunset and sunrise times API results.
 * Created by RacZo on 9/4/15.
 */
public class SunResults {

    private Date sunrise;
    private Date sunset;
    @SerializedName("solar_noon")
    private Date solarNoon;
    @SerializedName("day_length")
    private long dayLength;
    @SerializedName("civil_twilight_begin")
    private Date civilTwilightBegin;
    @SerializedName("civil_twilight_end")
    private Date civilTwilightEnd;
    @SerializedName("nautical_twilight_begin")
    private Date nauticalTwilighBegin;
    @SerializedName("nautical_twilight_end")
    private Date nauticalTwilightEnd;
    @SerializedName("astronomical_twilight_begin")
    private Date astronomicalTwilightBegin;
    @SerializedName("astronomical_twilight_end")
    private Date astronomicalTwilightEnd;

    public Date getSunrise() {
        return sunrise;
    }

    public void setSunrise(Date sunrise) {
        this.sunrise = sunrise;
    }

    public Date getSunset() {
        return sunset;
    }

    public void setSunset(Date sunset) {
        this.sunset = sunset;
    }

    public Date getSolarNoon() {
        return solarNoon;
    }

    public void setSolarNoon(Date solarNoon) {
        this.solarNoon = solarNoon;
    }

    public Date getCivilTwilightBegin() {
        return civilTwilightBegin;
    }

    public void setCivilTwilightBegin(Date civilTwilightBegin) {
        this.civilTwilightBegin = civilTwilightBegin;
    }

    public Date getCivilTwilightEnd() {
        return civilTwilightEnd;
    }

    public void setCivilTwilightEnd(Date civilTwilightEnd) {
        this.civilTwilightEnd = civilTwilightEnd;
    }

    public Date getNauticalTwilighBegin() {
        return nauticalTwilighBegin;
    }

    public void setNauticalTwilighBegin(Date nauticalTwilighBegin) {
        this.nauticalTwilighBegin = nauticalTwilighBegin;
    }

    public Date getNauticalTwilightEnd() {
        return nauticalTwilightEnd;
    }

    public void setNauticalTwilightEnd(Date nauticalTwilightEnd) {
        this.nauticalTwilightEnd = nauticalTwilightEnd;
    }

    public Date getAstronomicalTwilightBegin() {
        return astronomicalTwilightBegin;
    }

    public void setAstronomicalTwilightBegin(Date astronomicalTwilightBegin) {
        this.astronomicalTwilightBegin = astronomicalTwilightBegin;
    }

    public Date getAstronomicalTwilightEnd() {
        return astronomicalTwilightEnd;
    }

    public void setAstronomicalTwilightEnd(Date astronomicalTwilightEnd) {
        this.astronomicalTwilightEnd = astronomicalTwilightEnd;
    }

    public long getDayLength() {
        return dayLength;
    }

    public void setDayLength(long dayLength) {
        this.dayLength = dayLength;
    }
}
