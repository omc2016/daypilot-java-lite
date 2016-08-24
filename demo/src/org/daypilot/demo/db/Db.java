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

package org.daypilot.demo.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.daypilot.date.DateTime;
import org.daypilot.json.JSONException;

public class Db {

	
	/**
	 * @param from
	 * @param to
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 * @throws ClassNotFoundException
	 */
	public static Table getEvents(HttpServletRequest request, Date from, Date to) throws SQLException, JSONException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");

		PreparedStatement st = c.prepareStatement("SELECT event_id, event_name, event_start, event_end FROM EVENTS WHERE NOT ((event_end <= ?) OR (event_start >= ?));");
		st.setTimestamp(1, new Timestamp(from.getTime()), Calendar.getInstance(DateTime.UTC));
		st.setTimestamp(2, new Timestamp(to.getTime()), Calendar.getInstance(DateTime.UTC));
		ResultSet rs = st.executeQuery();
		Table table = TableLoader.load(rs);
		
		rs.close();
		st.close();
		c.close();
		
		return table; 
	}
	
	public static Table getEvents(HttpServletRequest request) throws SQLException, JSONException, ClassNotFoundException {
		Timestamp start = new DateTime("2008-01-01T00:00:00").toTimeStamp();
		Timestamp end = new DateTime("2010-01-01T00:00:00").toTimeStamp();
		return getEvents(request, start, end);
	}
	

	public static void createTable(HttpServletRequest request) throws SQLException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
		Statement st = c.createStatement();
		st.execute("CREATE TABLE EVENTS (event_id VARCHAR(200), event_name VARCHAR(200), event_start TIMESTAMP, event_end TIMESTAMP);");
		st.close();

		c.close();
		
		createSampleEvents(request);
	}
	
	public static void createSampleEvents(HttpServletRequest request) throws ClassNotFoundException, SQLException {
		insertEvent(request, "Event 1", new DateTime().toTimeStamp(), new DateTime().addMinutes(30).toTimeStamp());
		insertEvent(request, "Event 2", new DateTime().addDays(1).toTimeStamp(), new DateTime().addDays(1).addMinutes(30).toTimeStamp());
		insertEvent(request, "Event 3", new DateTime().addDays(-1).toTimeStamp(), new DateTime().addDays(-1).addHours(3).toTimeStamp());
		insertEvent(request, "Event 4", new DateTime().getDatePart().addHours(11).toTimeStamp(), new DateTime().getDatePart().addHours(14).toTimeStamp());
	}
	
	public static String insertEvent(HttpServletRequest request, String name, Date start, Date end) throws ClassNotFoundException, SQLException {
		String id = UUID.randomUUID().toString();
		
		Class.forName("org.hsqldb.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
		PreparedStatement st = c.prepareStatement("INSERT INTO EVENTS (event_id, event_name, event_start, event_end) VALUES (?, ?, ?, ?);");
		st.setString(1, id);
		st.setString(2, name);
		st.setTimestamp(3, new Timestamp(start.getTime()), Calendar.getInstance(DateTime.UTC));
		st.setTimestamp(4, new Timestamp(end.getTime()), Calendar.getInstance(DateTime.UTC));
		st.execute();
		st.close();
		c.close();
		
		return id;
	}
	
	public static void deleteEvent(HttpServletRequest request, String id) throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
		PreparedStatement st = c.prepareStatement("DELETE FROM events WHERE event_id = ?;");
		st.setString(1, id);
		st.execute();
		st.close();
		c.close();
	}


	public static boolean tableExists(HttpServletRequest request, String name) throws SQLException, JSONException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery("SELECT count(*) FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE table_name = '" + name.toUpperCase() + "'");

		boolean result = false;
		if (rs.next()) {
			int count = rs.getInt(1);
			result = count == 1;
		}
		
		rs.close();
		st.close();
		c.close();
		
		return result;
	}


	public static void moveEvent(HttpServletRequest request, String id, Timestamp start,
			Timestamp end) throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
		PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_start = ?, event_end = ? WHERE event_id = ?;");
		st.setTimestamp(1, start, Calendar.getInstance(DateTime.UTC));
		st.setTimestamp(2, end, Calendar.getInstance(DateTime.UTC));
		st.setString(3, id);
		st.execute();
		st.close();
		c.close();
	}

	public static void resizeEvent(HttpServletRequest request, String id, Timestamp start,
			Timestamp end) throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
		PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_start = ?, event_end = ? WHERE event_id = ?;");
		st.setTimestamp(1, start, Calendar.getInstance(DateTime.UTC));
		st.setTimestamp(2, end, Calendar.getInstance(DateTime.UTC));
		st.setString(3, id);
		st.execute();
		st.close();
		c.close();
	}
	
	private static String getConnectionString(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		
		if (request.getSession().getAttribute("cs") == null) {
			ServletContext context = session.getServletContext();
			String path = context.getRealPath("/WEB-INF/data/" + new DateTime().toDateString()); 
			
			try {
				new File(path).mkdirs();
			}
			catch (Exception e) { // unable to create the on-disk database
				path = null;
			}
			
			String cs;
			if (path == null) {
				cs = "jdbc:hsqldb:mem:daypilot";
			}
			else {
				cs = "jdbc:hsqldb:file:" + path + "/" + session.getId();
			}
			
			session.setAttribute("cs", cs);
			
		}
		
		String cs = (String) session.getAttribute("cs"); 
		return cs;
	}

	public static void updateEventText(HttpServletRequest request,
			String value, String newText) throws SQLException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
		PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_name = ? WHERE event_id = ?;");
		st.setString(1, newText);
		st.setString(2, value);
		st.execute();
		st.close();
		c.close();
	}

	public static Row getEvent(HttpServletRequest request, String id) {
		try {
			Class.forName("org.hsqldb.jdbcDriver" );
			Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
	
			PreparedStatement st = c.prepareStatement("SELECT event_id, event_name, event_start, event_end FROM EVENTS WHERE event_id = ?;");
			st.setString(1, id);
			ResultSet rs = st.executeQuery();
			Table table = TableLoader.load(rs);
			
			rs.close();
			st.close();
			c.close();
	
			if (table.size() > 0) {
				return table.get(0);
			}
			return null;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void updateEvent(HttpServletRequest request, String id, String name,
			Timestamp start, Timestamp end, String resource) {

		try {
			Class.forName("org.hsqldb.jdbcDriver" );
			Connection c = DriverManager.getConnection(getConnectionString(request), "sa", "");
			PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_start = ?, event_end = ?, event_name = ? WHERE event_id = ?;");
			st.setTimestamp(1, start, Calendar.getInstance(DateTime.UTC));
			st.setTimestamp(2, end, Calendar.getInstance(DateTime.UTC));
			st.setString(3, name);
			st.setString(4, id);
			st.execute();
			st.close();
			c.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}
