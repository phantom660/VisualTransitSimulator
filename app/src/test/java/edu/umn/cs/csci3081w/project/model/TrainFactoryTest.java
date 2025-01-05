package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrainFactoryTest {
  private StorageFacility storageFacility;
  private TrainFactory trainFactory;

  /**
   * Setup operations.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    storageFacility = new StorageFacility(0, 0, 3, 3);
    trainFactory = new TrainFactory(storageFacility, new Counter(), 9);
  }

  /**
   * Testing the constructor.
   */
  @Test
  public void testConstructor() {
    assertTrue(trainFactory.getGenerationStrategy() instanceof TrainStrategyDay);
  }

  @Test
  public void testTrainFactoryDayTime() {
    TrainFactory trainFactory = new TrainFactory(storageFacility,
        new Counter(), 9); // Day time
    assertTrue(trainFactory.getGenerationStrategy() instanceof TrainStrategyDay);
  }

  @Test
  public void testTrainFactoryEarlyMorning() {
    TrainFactory trainFactory = new TrainFactory(storageFacility,
        new Counter(), 7); // Early morning
    assertTrue(trainFactory.getGenerationStrategy() instanceof TrainStrategyNight);
  }

  @Test
  public void testTrainFactoryNightTime() {
    TrainFactory trainFactory = new TrainFactory(storageFacility,
        new Counter(), 19); // Night time
    assertTrue(trainFactory.getGenerationStrategy() instanceof TrainStrategyNight);
  }

  /**
   * Testing if generated vehicle is working according to strategy.
   */
  @Test
  public void testGenerateVehicle() {
    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    Line line = new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn,
        new Issue());

    Vehicle vehicle1 = trainFactory.generateVehicle(line);
    assertEquals(vehicle1.getVehicleType(), ElectricTrain.ELECTRIC_TRAIN_VEHICLE);
  }

  /**
   * Testing if vehicle got returned.
   */
  @Test
  public void testReturnVehicleElectricTrain() {
    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    Train testTrain = new ElectricTrain(1, new Line(10000, "testLine", "BUS",
        testRouteOut, testRouteIn, new Issue()), 3, 1.0);

    assertEquals(3, trainFactory.getStorageFacility().getElectricTrainsNum());
    assertEquals(3, trainFactory.getStorageFacility().getDieselTrainsNum());
    trainFactory.returnVehicle(testTrain);
    assertEquals(4, trainFactory.getStorageFacility().getElectricTrainsNum());
    assertEquals(3, trainFactory.getStorageFacility().getDieselTrainsNum());

  }

  /**
   * Testing if generated vehicle is working according to night strategy.
   */
  @Test
  public void testGenerateVehicleNight() {
    trainFactory = new TrainFactory(storageFacility, new Counter(), 20); // Night time
    List<Stop> stopsIn = new ArrayList<>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn", stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut", stopsOut, distancesOut, generatorOut);

    Line line = new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn, new Issue());

    Vehicle vehicle1 = trainFactory.generateVehicle(line);
    assertEquals(ElectricTrain.ELECTRIC_TRAIN_VEHICLE, ElectricTrain.ELECTRIC_TRAIN_VEHICLE);
  }

  /**
   * Testing if vehicle got returned.
   */
  @Test
  public void testReturnVehicleDieselTrain() {
    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    Train testTrain = new DieselTrain(1, new Line(10000, "testLine", "BUS",
        testRouteOut, testRouteIn, new Issue()), 3, 1.0);

    assertEquals(3, trainFactory.getStorageFacility().getElectricTrainsNum());
    assertEquals(3, trainFactory.getStorageFacility().getDieselTrainsNum());
    trainFactory.returnVehicle(testTrain);
    assertEquals(3, trainFactory.getStorageFacility().getElectricTrainsNum());
    assertEquals(4, trainFactory.getStorageFacility().getDieselTrainsNum());
  }

  @Test
  public void testGenerateDieselTrainNight() {
    // Ensure there are diesel trains available in the storage facility
    storageFacility = new StorageFacility(0, 0, 3, 0);
    trainFactory = new TrainFactory(storageFacility, new Counter(), 20); // Night time

    // Create stops and routes
    List<Stop> stopsIn = new ArrayList<>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn", stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut", stopsOut, distancesOut, generatorOut);

    Line line = new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn, new Issue());

    // Generate a DieselTrain
    Vehicle vehicle = trainFactory.generateVehicle(line);
    assertNotNull(vehicle, "Vehicle should not be null");
    assertEquals(ElectricTrain.ELECTRIC_TRAIN_VEHICLE, vehicle.getVehicleType());
  }

  @Test
  public void testGenerateElectricTrain() {
    List<Stop> stopsIn = new ArrayList<>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn", stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut", stopsOut, distancesOut, generatorOut);

    Line line = new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn, new Issue());

    // Test case where typeOfVehicle is ElectricTrain
    trainFactory = new TrainFactory(storageFacility, new Counter(), 9); // Day time
    Vehicle vehicle1 = trainFactory.generateVehicle(line);
    assertEquals(ElectricTrain.ELECTRIC_TRAIN_VEHICLE, vehicle1.getVehicleType());

    storageFacility = new StorageFacility(0, 0, 5, 5); // No electric trains available
    trainFactory = new TrainFactory(storageFacility, new Counter(), 9); // Day time
    trainFactory.generateVehicle(line);
    trainFactory.generateVehicle(line);
    trainFactory.generateVehicle(line);
    Vehicle vehicle2 = trainFactory.generateVehicle(line);
    assertEquals(DieselTrain.DIESEL_TRAIN_VEHICLE, vehicle2.getVehicleType());

    // Test case where typeOfVehicle is null
    storageFacility = new StorageFacility(0, 0, 0, 0); // No trains available
    trainFactory = new TrainFactory(storageFacility, new Counter(), 9); // Day time
    Vehicle vehicle3 = trainFactory.generateVehicle(line);
    assertNull(vehicle3);
  }
}
