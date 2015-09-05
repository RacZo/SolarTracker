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
package com.oscarsalguero.solartracker;

/**
 * Constants
 * Created by RacZo on 9/4/15.
 */
public class Constants {

    public static final String API_BASE_URL = "http://api.sunrise-sunset.org/json?date=today&formatted=0&";
    public static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss+00:00";
    public static final String API_STATUS_OK = "OK";
    public static final String API_STATUS_INVALID_REQUEST = "INVALID_REQUEST";
    public static final String API_STATUS_INVALID_DATE = "INVALID_DATE";
    public static final String API_STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";

    public static final int DEFAULT_TIMEOUT_MS = 5000;

    public static final String RESPONSE_RESULTS = "results";
    public static final String RESPONSE_STATUS = "status";

    public static final String DATE_TIME_DISPLAY_FORMAT = "HH:mm:ss";

}
