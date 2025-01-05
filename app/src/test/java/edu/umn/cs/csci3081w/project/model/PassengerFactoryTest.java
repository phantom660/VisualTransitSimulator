package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PassengerFactoryTest {

  /**
   * Setup deterministic operations before each test run.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
  }

  /**
   * Tests generate function.
   */
  @Test
  public void testGenerate() {
    assertEquals(3, PassengerFactory.generate(1, 10).getDestination());

    int count = PassengerFactory.DETERMINISTIC_DESTINATION_COUNT;
    PassengerFactory.DETERMINISTIC = false;

    PassengerFactory.generate(1, 10);
    assertEquals(count, PassengerFactory.DETERMINISTIC_DESTINATION_COUNT);
  }

  /**
   * Tests generate function.
   */
  @Test
  public void nameGeneration() {
    assertEquals("Goldy", PassengerFactory.nameGeneration());

    int count = PassengerFactory.DETERMINISTIC_NAMES_COUNT;
    PassengerFactory.DETERMINISTIC = false;

    PassengerFactory.nameGeneration();
    assertEquals(count, PassengerFactory.DETERMINISTIC_NAMES_COUNT);
  }

}
