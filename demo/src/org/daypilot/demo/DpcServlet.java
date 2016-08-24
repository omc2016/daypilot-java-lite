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

package org.daypilot.demo;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.date.DateTime;
import org.daypilot.date.Week;
import org.daypilot.demo.db.Db;
import org.daypilot.ui.DayPilotCalendar;
import org.daypilot.ui.args.calendar.CommandArgs;
import org.daypilot.ui.args.calendar.EventMoveArgs;
import org.daypilot.ui.args.calendar.EventResizeArgs;
import org.daypilot.ui.args.calendar.TimeRangeSelectedArgs;
import org.daypilot.ui.enums.UpdateType;
import org.daypilot.ui.enums.ViewType;

public class DpcServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		DayPilotCalendar dpc = new Dpc();
		dpc.process(request, response);

	}
	
	public class Dpc extends DayPilotCalendar {
		
		@Override
		public void onPrepare() throws Exception {
			// create the in-memory DB if it's not ready
			if (!Db.tableExists(getRequest(), "EVENTS")) {
				Db.createTable(getRequest());
			}
			
			setLocale(Locale.US);
		}
		
		@Override
		public void onEventMove(EventMoveArgs ea) throws Exception {
			// update the DB
			Db.moveEvent(getRequest(), ea.getValue(), ea.getNewStart().toTimeStamp(), ea.getNewEnd().toTimeStamp());
			update();
		}
		
		@Override
		public void onEventResize(EventResizeArgs ea) throws Exception {
			Db.resizeEvent(getRequest(), ea.getValue(), ea.getNewStart().toTimeStamp(), ea.getNewEnd().toTimeStamp());
			update();
		}
		
		@Override
		public void onTimeRangeSelected(TimeRangeSelectedArgs ea) throws Exception {
			Db.insertEvent(getRequest(), "New event", ea.getStart().toDate(), ea.getEnd().toDate());
			update();
		}
		
		@Override
		public void onInit() {
			update(UpdateType.FULL);
		}
		
		@Override
		public void onCommand(CommandArgs ea) throws Exception {
			if ("navigate".equals(ea.getCommand())) {
				DateTime start = DateTime.parseString(ea.getData().getString("start"));
				
				start = Week.firstDayOfWeek(start, getFirstDayOfWeek());
				
				setStartDate(start);
				update(UpdateType.FULL);
			}
			else if ("day".equals(ea.getCommand())) {
				setViewType(ViewType.DAY);
				update(UpdateType.FULL);
			}
			else if ("week".equals(ea.getCommand())) {
				setViewType(ViewType.WEEK);
				update(UpdateType.FULL);
			}
			else if ("previous".equals(ea.getCommand())) {
				switch (getViewType()) {
					case DAY:
						setStartDate(getStartDate().addDays(-1));
						break;
					case WEEK:
						setStartDate(getStartDate().addDays(-7));
						break;
				}
				update(UpdateType.FULL);
			}
			else if ("next".equals(ea.getCommand())) {
				switch (getViewType()) {
					case DAY:
						setStartDate(getStartDate().addDays(1));
						break;
					case WEEK:
						setStartDate(getStartDate().addDays(7));
						break;
				}
				update(UpdateType.FULL);
				
			}
			else if ("today".equals(ea.getCommand())) {
				setStartDate(DateTime.getToday());
				update(UpdateType.FULL);
			}
			/*
			else if ("height".equals(ea.getCommand())) {
				setHeightSpec(HeightSpec.parse(ea.getData().getString("value")));
				update(UpdateType.FULL);
			}*/
		}
		
		@Override
		public void onFinish() throws Exception {

			// load the events only if update was requested
			if (getUpdateType() == UpdateType.NONE) {
				return;
			}
			
			// set the database fields
			setDataValueField("event_id");
			setDataTextField("event_name");
			setDataStartField("event_start");
			setDataEndField("event_end");
			
			setEvents(Db.getEvents(getRequest(), getStartDate().toDate(), getStartDate().addDays(getDays()).toDate()));
			
		}
		
	}
}
