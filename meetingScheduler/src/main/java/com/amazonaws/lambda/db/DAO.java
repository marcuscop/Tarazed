package com.amazonaws.lambda.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.model.Schedule;
import com.amazonaws.lambda.model.TimeSlot;

public class DAO {

	java.sql.Connection conn;

    public DAO() {
    	try  {
//    		System.out.println("\n\n\n");
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
//    		e.printStackTrace();
    		conn = null;
    	}
    }
    
    
    public boolean addSchedule(Schedule schedule) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Schedules (id, secretcode, startdate, enddate, daystarthour, dayendhour, organizer) values(?,?,?,?,?,?,?);");
            System.out.println("\n\n\n");
            ps.setString(1, schedule.id);
            ps.setString(2, schedule.getsecretcode());
            ps.setString(3, schedule.startdate);
            ps.setString(4, schedule.enddate);
            ps.setString(5, schedule.daystarthour);
            ps.setString(6, schedule.dayendhour);
//            ps.setDate(3, java.sql.Date.valueOf(schedule.startdate));
//            ps.setDate(4, java.sql.Date.valueOf(schedule.enddate));
//            ps.setTime(5, java.sql.Time.valueOf(schedule.daystarthour));
//            ps.setTime(6, java.sql.Time.valueOf(schedule.dayendhour));
            ps.setString(7, schedule.organizer);
            ps.execute();
            return true;

        } catch (Exception e) {
//        	e.printStackTrace();
            throw new Exception("Failed to insert schedule: " + e.getMessage());
        }
    }

}