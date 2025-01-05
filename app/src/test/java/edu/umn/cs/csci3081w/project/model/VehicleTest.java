package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.webserver.WebServerSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class VehicleTest {

  private Vehicle testVehicle;
  private Route testRouteIn;
  private Route testRouteOut;
  private WebServerSession mockSession;
  private VehicleConcreteSubject vehicleConcreteSubject;


  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;

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

    testRouteIn = new Route(0, "testRouteIn",
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

    testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    testVehicle = new VehicleTestImpl(1, new Line(10000, "testLine",
        "VEHICLE_LINE", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0, new PassengerLoader(), new PassengerUnloader());

    mockSession = mock(WebServerSession.class);
    vehicleConcreteSubject = new VehicleConcreteSubject(mockSession);
    testVehicle.setVehicleSubject(vehicleConcreteSubject);
  }

  /**
   * Tests constructor.
   */
  @Test
  public void testConstructor() {
    assertEquals(1, testVehicle.getId());
    assertEquals("testRouteOut1", testVehicle.getName());
    assertEquals(3, testVehicle.getCapacity());
    assertEquals(1, testVehicle.getSpeed());
    assertEquals(testRouteOut, testVehicle.getLine().getOutboundRoute());
    assertEquals(testRouteIn, testVehicle.getLine().getInboundRoute());
  }

  /**
   * Tests if testIsTripComplete function works properly.
   */
  @Test
  public void testIsTripComplete() {
    assertEquals(false, testVehicle.isTripComplete());
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    assertEquals(true, testVehicle.isTripComplete());

  }


  /**
   * Tests if loadPassenger function works properly.
   */
  @Test
  public void testLoadPassenger() {

    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    Passenger testPassenger2 = new Passenger(2, "testPassenger2");
    Passenger testPassenger3 = new Passenger(1, "testPassenger3");
    Passenger testPassenger4 = new Passenger(1, "testPassenger4");

    assertEquals(1, testVehicle.loadPassenger(testPassenger1));
    assertEquals(1, testVehicle.loadPassenger(testPassenger2));
    assertEquals(1, testVehicle.loadPassenger(testPassenger3));
    assertEquals(0, testVehicle.loadPassenger(testPassenger4));
  }


  /**
   * Tests if move function works properly.
   */
  @Test
  public void testMove() {

    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.move();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals(null, testVehicle.getNextStop());

  }

  /**
   * Tests if update function works properly.
   */
  @Test
  public void testUpdate() {

    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.update();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals(null, testVehicle.getNextStop());

  }

  /**
   * Test to see if observer got attached.
   */
  @Test
  public void testProvideInfo() {
    VehicleConcreteSubject vehicleConcreteSubjectStub = mock(VehicleConcreteSubject.class);
    WebServerSession webServerSessionDummy = mock(WebServerSession.class);
    doReturn(webServerSessionDummy).when(vehicleConcreteSubjectStub).getSession();
    testVehicle.setVehicleSubject(vehicleConcreteSubjectStub);
    testVehicle.update();
    testVehicle.provideInfo();
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionDummy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    String command = commandToClient.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = commandToClient.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: " + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 0" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Tests the information provided for different vehicle types.
   * The test verifies that the correct data is sent for various vehicle types,
   * including SmallBus, LargeBus, ElectricTrain,
   * and DieselTrain. Each vehicle's information is checked to
   * ensure the correct command and details are sent, such as vehicle
   * type, position, passengers, and CO2 emissions.
   */
  @Test
  public void testProvideInfoForDifferentVehicleTypes() {
    // Test for SmallBus
    Vehicle smallBus = new SmallBus(1, new Line(10000, "testLine", "VEHICLE_LINE",
        testRouteOut, testRouteIn, new Issue()), 3, 1.0);
    smallBus.setVehicleSubject(vehicleConcreteSubject);
    smallBus.provideInfo();
    JsonObject expectedDataSmallBus = new JsonObject();
    expectedDataSmallBus.addProperty("command", "observedVehicle");
    expectedDataSmallBus.addProperty("text", "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: SMALL_BUS_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: " + System.lineSeparator());
    verify(mockSession).sendJson(expectedDataSmallBus);

    // Test for LargeBus
    Vehicle largeBus = new LargeBus(2, new Line(10001, "testLine", "VEHICLE_LINE",
        testRouteOut, testRouteIn, new Issue()), 5, 1.5);
    largeBus.setVehicleSubject(vehicleConcreteSubject);
    largeBus.provideInfo();
    JsonObject expectedDataLargeBus = new JsonObject();
    expectedDataLargeBus.addProperty("command", "observedVehicle");
    expectedDataLargeBus.addProperty("text", "2" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: LARGE_BUS_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: " + System.lineSeparator());
    verify(mockSession).sendJson(expectedDataLargeBus);

    // Test for ElectricTrain
    Vehicle electricTrain = new ElectricTrain(3, new Line(10002, "testLine", "VEHICLE_LINE",
        testRouteOut, testRouteIn, new Issue()), 10, 2.0);
    electricTrain.setVehicleSubject(vehicleConcreteSubject);
    electricTrain.provideInfo();
    JsonObject expectedDataElectricTrain = new JsonObject();
    expectedDataElectricTrain.addProperty("command", "observedVehicle");
    expectedDataElectricTrain.addProperty("text", "3" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: ELECTRIC_TRAIN_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: " + System.lineSeparator());
    verify(mockSession).sendJson(expectedDataElectricTrain);

    // Test for DieselTrain
    Vehicle dieselTrain = new DieselTrain(4, new Line(10003, "testLine", "VEHICLE_LINE",
        testRouteOut, testRouteIn, new Issue()), 8, 1.8);
    dieselTrain.setVehicleSubject(vehicleConcreteSubject);
    dieselTrain.provideInfo();
    JsonObject expectedDataDieselTrain = new JsonObject();
    expectedDataDieselTrain.addProperty("command", "observedVehicle");
    expectedDataDieselTrain.addProperty("text", "4" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: DIESEL_TRAIN_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: " + System.lineSeparator());
    verify(mockSession).sendJson(expectedDataDieselTrain);
  }

  /**
   * Tests the behavior when a vehicle's trip is complete.
   * The test simulates the vehicle moving until the trip is complete,
   * then verifies that the vehicle provides the correct
   * information and sends an empty "observedVehicle" message.
   */
  @Test
  public void testProvideInfoWhenTripComplete() {
    while (!testVehicle.isTripComplete()) {
      testVehicle.move();
    }
    boolean tripCompleted = testVehicle.provideInfo();
    assertEquals(true, tripCompleted);
    JsonObject expectedData = new JsonObject();
    expectedData.addProperty("command", "observedVehicle");
    expectedData.addProperty("text", "");
    verify(mockSession).sendJson(expectedData);
  }

  /**
   * Tests the construction of the carbon emission history string.
   * The test updates the vehicle multiple times,
   * then verifies that the CO2 emission history is correctly built and
   * sent as part of the "observedVehicle" message.
   */
  @Test
  public void testCarbonEmissionHistoryStringBuilding() {
    testVehicle.update();
    testVehicle.update();
    testVehicle.update();
    testVehicle.provideInfo();
    JsonObject expectedData = new JsonObject();
    expectedData.addProperty("command", "observedVehicle");
    expectedData.addProperty("text", "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: " + System.lineSeparator()
        + "* Position: (-93.243774,44.972392)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 0, 0, 0" + System.lineSeparator());
    verify(mockSession).sendJson(expectedData);
  }

  /**
   * Tests the vehicle's behavior when the remaining distance is set to a negative value.
   * The test sets a negative remaining distance,
   * moves the vehicle, and verifies that the remaining distance is
   * correctly adjusted to 0 after the move.
   */
  @Test
  public void testMoveWithNegativeRatio() {
    testVehicle.setDistanceRemaining(-1);
    double nextStopDistance = testVehicle.getLine().getOutboundRoute().getNextStopDistance();
    testVehicle.move();
    double roundedDistanceRemaining = Math.floor(testVehicle.getDistanceRemaining());
    assertEquals(0, roundedDistanceRemaining);
  }

  /**
   * Tests the vehicle's movement when handling passengers at a stop.
   * The test ensures that when the vehicle is at a stop,
   * it moves correctly and the remaining distance remains at 0.
   */
  @Test
  public void testMoveWithPassengersHandled() {
    // Set up the vehicle to handle passengers
    testVehicle.setDistanceRemaining(0);
    // Ensure the vehicle is at a stop to handle passengers
    testVehicle.move();
    double roundedDistanceRemaining = Math.floor(testVehicle.getDistanceRemaining());
    assertEquals(0, roundedDistanceRemaining);
  }

  /**
   * Tests the vehicle's movement until the trip is complete.
   * The test verifies that the vehicle correctly completes its trip after successive movements.
   */
  @Test
  public void testMoveWithTripComplete() {
    // Move the vehicle until the trip is complete
    while (!testVehicle.isTripComplete()) {
      testVehicle.move();
    }
    // Verify that the trip is complete
    assertTrue(testVehicle.isTripComplete());
  }

  /**
   * Tests the vehicle's behavior when moving with a negative speed.
   * The test sets a negative speed for the vehicle
   * and verifies that the remaining distance does not change, ensuring
   * the vehicle does not move with a negative speed.
   */
  @Test
  public void testMoveWithNegativeSpeed() {
    // Set the vehicle speed to a negative value
    testVehicle.setSpeed(-1);
    // Ensure the distance remaining is positive to observe any changes
    testVehicle.setDistanceRemaining(0.843774422231134);
    double initialDistanceRemaining = testVehicle.getDistanceRemaining();
    testVehicle.move();
    // Verify that the vehicle did not move
    assertEquals(initialDistanceRemaining, testVehicle.getDistanceRemaining());
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testVehicle = null;
  }

}
