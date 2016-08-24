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

package org.daypilot.util;

import java.util.Locale;

public class LocaleParser {
	public static Locale parse(String name) {
		if (name == null) {
			throw new NullPointerException("Locale name not specified.");
		}
		if (name.length() != 5) {
			throw new IllegalArgumentException("Locale name cannot be parsed (use 'en-us' format).");
		}
		String language = name.substring(0, 2);
		String country = name.substring(3, 5);
		return new Locale(language, country);
	}
	
	public static boolean isValid(String name) {
		try {
			parse(name);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

}
