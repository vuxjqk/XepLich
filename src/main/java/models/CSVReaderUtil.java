/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author PC
 */
public class CSVReaderUtil {

    public static List<Room> readRooms() throws IOException, CsvException {
        List<Room> rooms = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("rooms.csv"))) {
            List<String[]> records = reader.readAll();
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                rooms.add(new Room(Integer.parseInt(record[0]), record[1]));
            }
        }

        return rooms;
    }

    public static List<Day> readDays() throws IOException, CsvException {
        List<Day> days = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("days.csv"))) {
            List<String[]> records = reader.readAll();
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                days.add(new Day(Integer.parseInt(record[0]), LocalDate.parse(record[1])));
            }
        }

        return days;
    }

    public static List<Doctor> readDoctors(List<Room> rooms, List<Day> days) throws IOException, CsvException {
        List<Doctor> doctors = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("doctors.csv"))) {
            List<String[]> records = reader.readAll();
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);

                List<String> roomsID = Arrays.asList(record[2].split(" "));
                List<Room> allowedRooms = rooms.stream()
                        .filter(room -> roomsID.contains(String.valueOf(room.roomID())))
                        .collect(Collectors.toList());

                List<String> daysID = record[3].isEmpty() ? List.of() : Arrays.asList(record[3].split(" "));
                List<Day> daysOff = days.stream()
                        .filter(day -> daysID.contains(String.valueOf(day.dayID())))
                        .collect(Collectors.toList());

                doctors.add(new Doctor(Integer.parseInt(record[0]), record[1], allowedRooms, daysOff));
            }
        }

        return doctors;
    }

    public static List<Schedule> readSchedules(List<Doctor> doctors, List<Room> rooms, List<Day> days)
            throws IOException, CsvException {
        List<Schedule> schedules = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("schedules.csv"))) {
            List<String[]> records = reader.readAll();
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);

                Doctor doctor = doctors.stream()
                        .filter(d -> d.doctorID() == Integer.parseInt(record[0]))
                        .findFirst()
                        .orElse(null);

                Room room = rooms.stream()
                        .filter(r -> r.roomID() == Integer.parseInt(record[1]))
                        .findFirst()
                        .orElse(null);

                Day day = days.stream()
                        .filter(d -> d.dayID() == Integer.parseInt(record[2]))
                        .findFirst()
                        .orElse(null);

                schedules.add(new Schedule(doctor, room, day));
            }
        }

        return schedules;
    }

    public static List<User> readUsers(List<Doctor> doctors) throws IOException, CsvException {
        List<User> users = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("users.csv"))) {
            List<String[]> records = reader.readAll();
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);

                Doctor doctor = doctors.stream()
                        .filter(d -> d.doctorID() == Integer.parseInt(record[0]))
                        .findFirst()
                        .orElse(null);

                users.add(new User(Integer.parseInt(record[0]), record[1], record[2], record[3], doctor));
            }
        }

        return users;
    }
}
