/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package models;

import java.util.List;

/**
 *
 * @author PC
 */
public record Doctor(int doctorID, String doctorName, List<Room> allowedRooms, List<Day> daysOff) {

}
