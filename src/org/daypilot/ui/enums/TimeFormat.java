/*
Copyright © 2012 Annpoint, s.r.o.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

-------------------------------------------------------------------------

NOTE: Reuse requires the following acknowledgement (see also NOTICE):
This product includes DayPilot (http://www.daypilot.org) developed by Annpoint, s.r.o.
*/

package org.daypilot.ui.enums;

import java.util.Locale;

public enum TimeFormat {
	CLOCK_12_HOURS, 

	CLOCK_24_HOURS,

    AUTO;
	
	public static TimeFormat parse(String string) {
		if (string == null) {
			throw new NullPointerException("Cannot parse null string.");
		}
		
		String uppercase = string.toUpperCase();
		if ("CLOCK12HOURS".equals(uppercase)) {
			return CLOCK_12_HOURS;
		}
		if ("CLOCK24HOURS".equals(uppercase)) {
			return CLOCK_24_HOURS;
		}
		if ("AUTO".equals(uppercase)) {
			return AUTO;
		}
		throw new IllegalArgumentException("Unable to parse the supplied string.");
	}
	
	@Override
	public String toString() {
		switch (this) {
			case CLOCK_12_HOURS:
				return "Clock12Hours";
			case CLOCK_24_HOURS:
				return "Clock24Hours";
			case AUTO:
				return "Auto";
			default:
				throw new IllegalArgumentException("Unknown TimeFormat value.");
		}
	}

	public static boolean isValid(String value) {
		try {
			parse(value);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public TimeFormat resolve(Locale locale)
    {
        if (this == TimeFormat.AUTO)
        {
        	return getDefaultTimeFormat(locale);
        }
        else
        {
            return this;
        }
    }
	
	private static TimeFormat getDefaultTimeFormat(Locale locale) {
		String country = locale.getCountry().toLowerCase();
		
		if ("au".equals(country)) {  // Australia
			return CLOCK_12_HOURS;
		}
		if ("bd".equals(country)) {  // Bangladesh
			return CLOCK_12_HOURS;
		}
		if ("ca".equals(country)) {  // Canada
			return CLOCK_12_HOURS;
		}
		if ("co".equals(country)) { // Colombia
			return CLOCK_12_HOURS;
		}
		if ("eg".equals(country)) { // Egypt
			return CLOCK_12_HOURS;
		}
		if ("sv".equals(country)) { // El Salvador
			return CLOCK_12_HOURS;
		}
		if ("hn".equals(country)) {  // Honduras
			return CLOCK_12_HOURS;
		}
		if ("in".equals(country)) {  // India
			return CLOCK_12_HOURS;
		}
		if ("my".equals(country)) {  // Malaysia
			return CLOCK_12_HOURS;
		}
		if ("nz".equals(country)) {  // New Zealand
			return CLOCK_12_HOURS;
		}
		if ("ni".equals(country)) {  // Nicaragua
			return CLOCK_12_HOURS;
		}
		if ("pk".equals(country)) {  // Pakistan
			return CLOCK_12_HOURS;
		}
		if ("ph".equals(country)) { // Philippines
			return CLOCK_12_HOURS;
		}
		if ("us".equals(country)) { // US
			return CLOCK_12_HOURS;
		}

		return CLOCK_24_HOURS;
	}

}
