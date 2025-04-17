/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author PC
 */
public class Main {

    public static void main(String[] args) throws IOException, CsvException {
        List<Room> rooms = CSVReaderUtil.readRooms();
        List<Day> days = CSVReaderUtil.readDays();
        List<Doctor> doctors = CSVReaderUtil.readDoctors(rooms, days);
        List<Schedule> schedules = CSVReaderUtil.readSchedules(doctors, rooms, days);
        List<User> users = CSVReaderUtil.readUsers(doctors);

        days = getDays(2025, 1);
        CSVWriterUtil.writeDays(days);

        CSVWriterUtil.writeDoctors(doctors);

        GeneticAlgorithm ga = new GeneticAlgorithm(doctors, rooms, days,
                200, 2000,
                3, 0.8, 0.2,
                new Random());
        schedules = ga.run();
        CSVWriterUtil.writeSchedules(schedules);
    }

    public static List<Day> getDays(int year, int month) {
        List<Day> days = new ArrayList<>();
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            days.add(new Day(day, date));
        }

        return days;
    }
}
