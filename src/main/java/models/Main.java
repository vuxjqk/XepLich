/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.opencsv.exceptions.CsvException;

/**
 *
 * @author PC
 */
public class Main {
    public static void main(String[] args) throws IOException, CsvException {
        Random random = new Random();
        List<Room> rooms = CSVReaderUtil.readRooms();
        List<Day> days = CSVReaderUtil.readDays();
        List<Doctor> doctors = CSVReaderUtil.readDoctors(rooms, days);
        List<User> users = CSVReaderUtil.readUsers(doctors);
        List<Schedule> schedules = CSVReaderUtil.readSchedules(doctors, rooms, days);

        GeneticAlgorithm ga = new GeneticAlgorithm(doctors, rooms, days,
                200, 2000, 3,
                0.8, 0.2, random);
        ga.run();
    }
}
