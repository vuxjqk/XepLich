/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author PC
 */
public record GeneticAlgorithm(
        List<Doctor> doctors,
        List<Room> rooms,
        List<Day> days,
        int populationSize,
        int generations,
        int tournamentSize,
        double cxpb,
        double mutpb,
        Random random) {

    private List<Schedule> createIndividual() {
        List<Schedule> individual = new ArrayList<>();

        for (Day day : days) {
            for (Room room : rooms) {
                Doctor doctor = doctors.get(random.nextInt(doctors.size()));
                individual.add(new Schedule(doctor, room, day));
            }
        }

        return individual;
    }

    private List<List<Schedule>> createPopulation() {
        Set<List<Schedule>> population = new HashSet<>();

        while (population.size() < populationSize) {
            population.add(createIndividual());
        }

        return new ArrayList<>(population);
    }

    private int fitness(List<Schedule> individual) {
        int violations = 0;
        Map<Doctor, TreeSet<Day>> doctorShifts = new HashMap<>();
        Map<Doctor, Integer> doctorShiftCount = new HashMap<>();

        for (Schedule schedule : individual) {
            Doctor doctor = schedule.doctor();
            Room room = schedule.room();
            Day day = schedule.day();

            if (!doctor.allowedRooms().contains(room)) {
                violations += 1000;
            }

            if (doctor.daysOff().contains(day)) {
                violations += 1000;
            }

            doctorShifts.computeIfAbsent(doctor, _ -> new TreeSet<>(Comparator.comparingInt(Day::dayID)));
            if (!doctorShifts.get(doctor).add(day)) {
                violations += 1000;
            }
        }

        for (Map.Entry<Doctor, TreeSet<Day>> doctorShiftEntry : doctorShifts.entrySet()) {
            Day prevDay = null;
            for (Day day : doctorShiftEntry.getValue()) {
                if (prevDay != null) {
                    int gap = day.dayID() - prevDay.dayID();
                    violations += 12 / gap;
                }
                prevDay = day;
            }

            doctorShiftCount.put(doctorShiftEntry.getKey(), doctorShiftEntry.getValue().size());
        }

        int avgShifts = doctorShiftCount.values().stream()
                .mapToInt(Integer::intValue)
                .sum() / doctors.size();

        for (int shiftCount : doctorShiftCount.values()) {
            violations += Math.abs(shiftCount - avgShifts);
        }

        return violations;
    }

    private List<Schedule> runTournament(List<List<Schedule>> population) {
        List<List<Schedule>> shuffled = new ArrayList<>(population);
        Collections.shuffle(shuffled, random);

        List<List<Schedule>> tournament = shuffled.subList(0, tournamentSize);

        return tournament.stream()
                .min(Comparator.comparingInt(this::fitness))
                .orElse(null);
    }

    private List<List<Schedule>> selection(List<List<Schedule>> population) {
        List<List<Schedule>> selected = new ArrayList<>();

        for (int i = 0; i < populationSize / 2; i++) {
            selected.add(runTournament(population));
        }

        return selected;
    }

    private List<List<Schedule>> crossover(List<List<Schedule>> parents) {
        if (random.nextDouble() < cxpb) {
            List<Schedule> child1 = new ArrayList<>(parents.get(0));
            List<Schedule> child2 = new ArrayList<>(parents.get(1));

            int point1 = random.nextInt(child1.size());
            int point2;
            do {
                point2 = random.nextInt(child2.size());
            } while (point1 == point2);

            int start = Math.min(point1, point2);
            int end = Math.max(point1, point2);

            for (int i = start; i < end; i++) {
                Schedule schedule1 = child1.get(i);
                Schedule schedule2 = child2.get(i);

                child1.set(i, new Schedule(schedule2.doctor(), schedule1.room(), schedule1.day()));
                child2.set(i, new Schedule(schedule1.doctor(), schedule2.room(), schedule2.day()));
            }

            return Arrays.asList(child1, child2);
        }

        return parents;
    }

    private List<Schedule> mutation(List<Schedule> individual) {
        if (random.nextDouble() < mutpb) {
            List<Schedule> mutated = new ArrayList<>(individual);
            int point = random.nextInt(mutated.size());
            Schedule schedule = mutated.get(point);

            Doctor doctor;
            do {
                doctor = doctors.get(random.nextInt(doctors.size()));
            } while (doctor.equals(schedule.doctor()));

            mutated.set(point, new Schedule(doctor, schedule.room(), schedule.day()));
            return mutated;
        }

        return individual;
    }

    public List<Schedule> run() {
        List<List<Schedule>> population = createPopulation();
        Map<List<Schedule>, Integer> fitnessCache = new HashMap<>();
        List<Schedule> elite = null;

        for (int generation = 1; generation <= generations; generation++) {
            fitnessCache.clear();
            for (List<Schedule> individual : population) {
                fitnessCache.put(individual, fitness(individual));
            }

            elite = fitnessCache.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            int bestFitness = fitnessCache.get(elite);

            String message = (generation % 100 == 1 || generation == generations)
                    ? String.format("Generation %d: Best fitness = %d, Unique individuals = %d",
                            generation, bestFitness, population.stream().distinct().count())
                    : String.format("Generation %d: Best fitness = %d",
                            generation, bestFitness);
            System.out.println(message);

            if (generation == generations) {
                break;
            }

            population = selection(population);

            List<List<Schedule>> offspring = new ArrayList<>();
            offspring.add(elite);

            while (offspring.size() < populationSize) {
                int index1 = random.nextInt(population.size());
                int index2;
                do {
                    index2 = random.nextInt(population.size());
                } while (index1 == index2);

                List<Schedule> parent1 = population.get(index1);
                List<Schedule> parent2 = population.get(index2);
                List<List<Schedule>> children = crossover(Arrays.asList(parent1, parent2));

                offspring.add(mutation(children.get(0)));
                if (offspring.size() < populationSize) {
                    offspring.add(mutation(children.get(1)));
                }
            }

            population = offspring;
        }

        return elite;
    }
}
