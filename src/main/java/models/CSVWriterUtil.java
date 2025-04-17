/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author PC
 */
public class CSVWriterUtil {

    public static void writeDays(List<Day> days) throws IOException {
        try (CSVWriter writer = new CSVWriter(
                new FileWriter("days.csv"),
                ',',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            String[] header = {"DayID", "Date"};
            writer.writeNext(header);

            for (Day day : days) {
                String[] row = {
                    String.valueOf(day.dayID()),
                    String.valueOf(day.date())
                };
                writer.writeNext(row);
            }
        }
    }

    public static void writeDoctors(List<Doctor> doctors) throws IOException {
        try (CSVWriter writer = new CSVWriter(
                new FileWriter("doctors.csv"),
                ',',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {
            String[] header = {"DoctorID", "DoctorName", "AllowedRooms", "DaysOff"};
            writer.writeNext(header);

            for (Doctor doctor : doctors) {
                String roomsID = String.join(" ", doctor.allowedRooms().stream()
                        .map(room -> String.valueOf(room.roomID()))
                        .toList());

                String daysID = String.join(" ", doctor.daysOff().stream()
                        .map(day -> String.valueOf(day.dayID()))
                        .toList());

                String[] row = {
                    String.valueOf(doctor.doctorID()),
                    doctor.doctorName(),
                    roomsID,
                    daysID
                };
                writer.writeNext(row);
            }
        }
    }

    public static void writeSchedules(List<Schedule> schedules) throws IOException {
        try (CSVWriter writer = new CSVWriter(
                new FileWriter("schedules.csv"),
                ',',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            String[] header = {"DoctorID", "RoomID", "DayID"};
            writer.writeNext(header);

            for (Schedule schedule : schedules) {
                String[] row = {
                    String.valueOf(schedule.doctor().doctorID()),
                    String.valueOf(schedule.room().roomID()),
                    String.valueOf(schedule.day().dayID())
                };
                writer.writeNext(row);
            }
        }
    }
}
