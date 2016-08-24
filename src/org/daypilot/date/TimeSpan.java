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

package org.daypilot.date;

public class TimeSpan {
	
	private long ticks = 0;
	private static final long TICKS_PER_DAY = 24 * 60 * 60 * 1000;
	private static final long TICKS_PER_HOUR = 60 * 60 * 1000;
	private static final long TICKS_PER_MINUTE = 60 * 1000;
	private static final long TICKS_PER_SECOND = 1000;
	
	public TimeSpan(long ticks) {
		this.ticks = ticks;
	}
	
	public static TimeSpan fromMinutes(int minutes) {
		return new TimeSpan(minutes * TICKS_PER_MINUTE);
	}
	
	public static TimeSpan fromHours(int hours) {
		return new TimeSpan(hours * TICKS_PER_HOUR);
	}
	
	public boolean isZero() {
		return ticks == 0;
	}

	public double getTotalDays() {
		return ticks / (double) TICKS_PER_DAY;
	}
	
	public long getDays() {
		return (long) Math.floor(getTotalDays());
	}
	
	public double getTotalHours() {
		return ticks / (double) TICKS_PER_HOUR;
	}

	public long getHours() {
		long remains = ticks % TICKS_PER_DAY;
		return (long) Math.floor(remains / (double) TICKS_PER_HOUR);
	}
	
	public double getTotalMinutes() {
		return ticks / (double) TICKS_PER_MINUTE;
	}
	
	public long getMinutes() {
		long remains = ticks % TICKS_PER_HOUR;
		return (long) Math.floor(remains / (double) TICKS_PER_MINUTE);
	}

	public double getTotalSeconds() {
		return ticks / (double) TICKS_PER_SECOND;
	}
	
	public long getSeconds() {
		long remains = ticks % TICKS_PER_MINUTE;
		return (long) Math.floor(remains / (double) TICKS_PER_SECOND);
	}
	
	public long getTotalMilliseconds() {
		return ticks;
	}

	public long getMilliSeconds() {
		return ticks % TICKS_PER_SECOND;
	}

	public long getTicks() {
		return ticks;
	}

	public static TimeSpan fromDays(int days) {
		return new TimeSpan(days * TICKS_PER_DAY);
	}
	
	public TimeSpan add(TimeSpan span) {
		return new TimeSpan(ticks + span.ticks);
	}
	
	public String getDayHourMinuteString() {
		if (getHours() == 0 && getDays() == 0) {
			return String.format("%dm", getMinutes());
		}
		if (getDays() == 0) {
			return String.format("%dh %02dm", getHours(), getMinutes());
		}
		return String.format("%dd %dh %02dm", getDays(), getHours(), getMinutes());
	}
	
	public String getHourMinuteString() {
		int hours = (int) getTotalHours();
		if (hours == 0) {
			return String.format("%dm", getMinutes());
		}
		return String.format("%dh %02dm", hours, getMinutes());
	}
	
	public String toString() {
		return String.format("%d:%02d:%02d:%02d", getDays(), getHours(), getMinutes(), getMinutes());
	}
	
}
