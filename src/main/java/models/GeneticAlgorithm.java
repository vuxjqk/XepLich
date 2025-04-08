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
        for (int i = 0; i < populationSize; i++) {
            population.add(createIndividual());
        }
        return new ArrayList<>(population);
    }

    private int fitness(List<Schedule> individual) {
        int violations = 0;

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
        }

        return violations;
    }

    private List<Schedule> runTournament(List<List<Schedule>> population, int tournamentSize) {
        List<List<Schedule>> shuffled = new ArrayList<>(population);
        Collections.shuffle(shuffled, random);

        List<List<Schedule>> tournament = shuffled.subList(0, Math.min(tournamentSize, shuffled.size()));

        return tournament.stream()
                .min(Comparator.comparingInt(this::fitness))
                .orElse(null);
    }

    private List<List<Schedule>> selection(List<List<Schedule>> population) {
        List<List<Schedule>> selected = new ArrayList<>();

        for (int i = 0; i < populationSize / 2; i++) {
            List<Schedule> winner = runTournament(population, tournamentSize);
            selected.add(winner);
        }

        return selected;
    }

    private List<List<Schedule>> crossover(List<List<Schedule>> parents) {
        if (random.nextDouble() < cxpb) {
            List<Schedule> child1 = new ArrayList<>(parents.get(0));
            List<Schedule> child2 = new ArrayList<>(parents.get(1));

            int size = child1.size();
            int point1 = random.nextInt(size);
            int point2;
            do {
                point2 = random.nextInt(size);
            } while (point1 == point2);

            int start = Math.min(point1, point2);
            int end = Math.max(point1, point2);

            for (int i = start; i < end; i++) {
                Schedule s1 = child1.get(i);
                Schedule s2 = child2.get(i);

                child1.set(i, new Schedule(s2.doctor(), s1.room(), s1.day()));
                child2.set(i, new Schedule(s1.doctor(), s2.room(), s2.day()));
            }

            return Arrays.asList(child1, child2);
        }

        return parents;
    }

    private List<Schedule> mutation(List<Schedule> individual) {
        if (random.nextDouble() < mutpb) {
            List<Schedule> mutated = new ArrayList<>(individual);
            int mutationPoint = random.nextInt(mutated.size());
            Schedule schedule = mutated.get(mutationPoint);

            Doctor doctor;
            do {
                doctor = doctors.get(random.nextInt(doctors.size()));
            } while (doctor.equals(schedule.doctor()));

            mutated.set(mutationPoint, new Schedule(doctor, schedule.room(), schedule.day()));
            return mutated;
        }

        return individual;
    }

    public List<Schedule> run() {
        List<List<Schedule>> population = createPopulation();

        for (int generation = 1; generation <= generations; generation++) {
            Map<List<Schedule>, Integer> fitnessMap = new HashMap<>();
            for (List<Schedule> individual : population) {
                fitnessMap.put(individual, fitness(individual));
            }

            int bestFitness = fitnessMap.values().stream()
                    .min(Integer::compareTo)
                    .orElse(Integer.MAX_VALUE);

            String message = (generation % 100 == 0 || generation == generations)
                    ? String.format("Generation %d: Best fitness = %d, Unique individuals = %d",
                            generation, bestFitness, population.stream().distinct().count())
                    : String.format("Generation %d: Best fitness = %d",
                            generation, bestFitness);
            System.out.println(message);

            List<Schedule> elite = fitnessMap.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            population = selection(population);

            List<List<Schedule>> offspring = new ArrayList<>();
            offspring.add(elite);

            while (offspring.size() < populationSize) {
                int parent1Index = random.nextInt(population.size());
                int parent2Index;
                do {
                    parent2Index = random.nextInt(population.size());
                } while (parent1Index == parent2Index);

                List<Schedule> parent1 = population.get(parent1Index);
                List<Schedule> parent2 = population.get(parent2Index);
                List<List<Schedule>> children = crossover(Arrays.asList(parent1, parent2));

                offspring.add(mutation(children.get(0)));
                if (offspring.size() < populationSize) {
                    offspring.add(mutation(children.get(1)));
                }
            }

            population = new ArrayList<>(offspring);
        }

        return population.stream()
                .min(Comparator.comparingInt(this::fitness))
                .orElse(null);
    }
}
