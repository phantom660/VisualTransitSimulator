package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.PassengerFactory;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.websocket.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class WebServerSessionTest {
  /**
   * Setup deterministic operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
  }

  /**
   * Test command for initializing the simulation.
   */
  @Test
  public void testSimulationInitialization() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "initLines");
    webServerSessionSpy.onMessage(commandFromClient.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("2", commandToClient.get("numLines").getAsString());
  }

  /**
   * Test the WebServerSession with incorrect command.
   */
  @Test
  public void testIncorrectCommand() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    Session session = mock(Session.class);
    webServerSessionSpy.onOpen(session);
    JsonObject command = new JsonObject();
    command.addProperty("command", "incorrectCommand");
    webServerSessionSpy.onMessage(command.toString());
  }

  // Helper method to set up the WebServerSession spy
  private WebServerSession setUpWebServerSessionSpy() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    return webServerSessionSpy;
  }


  /**
   * Tests vehicle retrieval and simulation updates in the {@link WebServerSession}.
   * The test sends a "getVehicles" command and verifies the
   * response. It then simulates vehicle updates by sending
   * "start" and "update" commands, verifying the vehicle updates after each cycle.
   */
  @Test
  public void testSimulationVehicleRetrieval() {
    WebServerSession webServerSessionSpy = setUpWebServerSessionSpy();

    sendCommandToServer(webServerSessionSpy, "getVehicles", null);
    verifyUpdateVehiclesResponse(webServerSessionSpy);

    JsonArray timeBetweenVehicles = new JsonArray();
    timeBetweenVehicles.add(1);
    timeBetweenVehicles.add(1);
    sendCommandToServer(webServerSessionSpy, "start", timeBetweenVehicles);

    for (int i = 1; i <= 20; i++) {
      sendCommandToServer(webServerSessionSpy, "update", null);
      sendCommandToServer(webServerSessionSpy, "getVehicles", null);
      verifyUpdateVehiclesResponse(webServerSessionSpy, i + 1);
    }
  }


  // Helper method to send commands to the server
  private void sendCommandToServer(WebServerSession webServerSessionSpy,
                                   String command, JsonArray timeBetweenVehicles) {
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", command);
    if (timeBetweenVehicles != null) {
      commandFromClient.add("timeBetweenVehicles", timeBetweenVehicles);
    }
    if (command.equals("start")) {
      commandFromClient.addProperty("numTimeSteps", 20);
    }
    webServerSessionSpy.onMessage(commandFromClient.toString());
  }

  // Helper method to verify the "updateVehicles" response
  private void verifyUpdateVehiclesResponse(WebServerSession webServerSessionSpy) {
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("updateVehicles", commandToClient.get("command").getAsString());
    assertNotNull(commandToClient.getAsJsonArray("vehicles"));
  }

  // Overloaded helper method to verify the
  // "updateVehicles" response with a different number of times
  private void verifyUpdateVehiclesResponse(WebServerSession webServerSessionSpy, int times) {
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy, times(times)).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("updateVehicles", commandToClient.get("command").getAsString());
    assertNotNull(commandToClient.getAsJsonArray("vehicles"));
  }

  /**
   * Tests the "getRoutes" command and verifies the response from the {@link WebServerSession}.
   * The test ensures that the "updateRoutes" command is
   * sent back with a non-null "routes" array after receiving a "getRoutes" request.
   */
  @Test
  public void testRouteRetrieval() {
    WebServerSession webServerSessionSpy = setUpWebServerSessionSpy();

    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "getRoutes");
    webServerSessionSpy.onMessage(commandFromClient.toString());

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());

    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("updateRoutes", commandToClient.get("command").getAsString());
    assertNotNull(commandToClient.getAsJsonArray("routes"));
  }


  /**
   * Tests handling of a "line issue" command in the {@link VisualTransitSimulator}.
   * The test verifies that no JSON messages are sent initially,
   * and after executing the command with {@link LineIssueCommand},
   * it checks that the line issue is not present on the specified line.
   *
   * @throws UnsupportedEncodingException If the configuration file encoding fails.
   */
  @Test
  public void testLineIssue() throws UnsupportedEncodingException {
    WebServerSession webServerSessionSpy = setUpWebServerSessionSpy();

    JsonObject commandFromClient = createLineIssueCommand(10001);
    webServerSessionSpy.onMessage(commandFromClient.toString());

    verify(webServerSessionSpy, never()).sendJson(Mockito.any(JsonObject.class));

    VisualTransitSimulator visualTransitSimulator =
        setUpVisualTransitSimulator(webServerSessionSpy);
    LineIssueCommand lineIssueCommand = new LineIssueCommand(visualTransitSimulator);
    Line newLine = visualTransitSimulator.getLines().get(1);
    visualTransitSimulator.getLines().clear();

    lineIssueCommand.execute(webServerSessionSpy, commandFromClient);

    assertFalse(newLine.isIssueExist());
  }


  /**
   * Creates a "lineIssue" command as a {@link JsonObject} with the specified line ID.
   *
   * @param lineId The ID of the line with the issue.
   * @return A {@link JsonObject} representing the line issue command.
   */
  private JsonObject createLineIssueCommand(int lineId) {
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "lineIssue");
    commandFromClient.addProperty("id", lineId);
    return commandFromClient;
  }

  /**
   * Sets up a {@link VisualTransitSimulator} using a
   * configuration file and the provided {@link WebServerSession}.
   *
   * @param webServerSessionSpy The {@link WebServerSession} spy.
   * @return A {@link VisualTransitSimulator} instance.
   * @throws UnsupportedEncodingException If UTF-8 encoding fails.
   */
  private VisualTransitSimulator setUpVisualTransitSimulator(
      WebServerSession webServerSessionSpy) throws UnsupportedEncodingException {
    return new VisualTransitSimulator(
        URLDecoder.decode(
            getClass().getClassLoader().getResource("config.txt").getFile(), "UTF-8"),
        webServerSessionSpy
    );
  }

  /**
   * Tests the start command and verifies that no JSON
   * messages are sent to the {@link WebServerSession}.
   * The test simulates receiving a start command from the
   * client and ensures that no further JSON messages are sent
   * by the {@link WebServerSession}.
   *
   * @see WebServerSession
   * @see JsonObject
   */
  @Test
  public void testStart() {
    WebServerSession webServerSessionSpy = setUpWebServerSessionSpy();

    JsonObject commandFromClient = createStartCommand(10, new int[]{5, 10, 15});
    webServerSessionSpy.onMessage(commandFromClient.toString());

    verify(webServerSessionSpy, never()).sendJson(Mockito.isA(JsonObject.class));
  }


  // Helper method to create the "start" command
  private JsonObject createStartCommand(int numTimeSteps, int[] timeBetweenVehicles) {
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "start");
    commandFromClient.addProperty("numTimeSteps", numTimeSteps);

    JsonArray times = new JsonArray();
    for (int time : timeBetweenVehicles) {
      times.add(time);
    }
    commandFromClient.add("timeBetweenVehicles", times);

    return commandFromClient;
  }

  /**
   * Tests the vehicle registration command and verifies the interaction
   * with the {@link WebServerSession}.
   * The test simulates starting the session, updating the vehicle status,
   * and registering a vehicle. It checks that
   * the {@link WebServerSession} sends a JSON message after the vehicle registration command.
   * Uses {@link ArgumentCaptor} to capture and verify the JSON message
   * sent to the {@link WebServerSession}.
   *
   * @see WebServerSession
   * @see JsonObject
   */
  @Test
  public void testVehicleRegistration() {
    // Arrange: Set up WebServerSession spy and session
    WebServerSession webServerSessionSpy = setUpWebServerSessionSpy();

    sendStartCommand(webServerSessionSpy, 10, new int[]{1, 1});

    sendUpdateCommand(webServerSessionSpy, 5);

    sendRegisterVehicleCommand(webServerSessionSpy, 1000);

    sendUpdateCommand(webServerSessionSpy, 1);

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
  }

  // Helper method to send the "start" command
  private void sendStartCommand(WebServerSession webServerSessionSpy,
                                int numTimeSteps, int[] timeBetweenVehicles) {
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "start");
    commandFromClient.addProperty("numTimeSteps", numTimeSteps);

    JsonArray timeBetweenVehiclesArray = new JsonArray();
    for (int time : timeBetweenVehicles) {
      timeBetweenVehiclesArray.add(time);
    }
    commandFromClient.add("timeBetweenVehicles", timeBetweenVehiclesArray);

    webServerSessionSpy.onMessage(commandFromClient.toString());
  }

  // Helper method to send the "update" command multiple times
  private void sendUpdateCommand(WebServerSession webServerSessionSpy, int times) {
    JsonObject updateCommand = new JsonObject();
    updateCommand.addProperty("command", "update");
    for (int i = 0; i < times; i++) {
      webServerSessionSpy.onMessage(updateCommand.toString());
    }
  }

  // Helper method to send the "registerVehicle" command
  private void sendRegisterVehicleCommand(WebServerSession webServerSessionSpy, int vehicleId) {
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "registerVehicle");
    commandFromClient.addProperty("id", vehicleId);
    webServerSessionSpy.onMessage(commandFromClient.toString());
  }

  /**
   * Tests various Vehicle Transit Simulator (VTS) commands and verifies
   * interactions with the {@link WebServerSession}.
   * This test simulates the following commands: start, update, register vehicle, pause,
   * and line issue, and checks that the
   * {@link WebServerSession} sends JSON messages as expected. The test ensures:
   * <ul>
   *     <li>No JSON messages are sent after the first update command.</li>
   *     <li>Two JSON messages are sent after subsequent commands (e.g., pause and update).</li>
   * </ul>
   * Uses {@link ArgumentCaptor} to verify the JSON messages sent to the {@link WebServerSession}.
   *
   * @see WebServerSession
   * @see JsonObject
   */
  @Test
  public void testAllCommands() {
    WebServerSession webServerSessionSpy = setUpWebServerSessionSpy();

    sendStartCommand(webServerSessionSpy, 12, new int[]{2, 2});

    sendUpdateCommand(webServerSessionSpy, 10);

    sendRegisterVehicleCommand(webServerSessionSpy, 1000);

    sendPauseCommand(webServerSessionSpy);

    sendUpdateCommand(webServerSessionSpy, 1);

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy, never()).sendJson(messageCaptor.capture());

    sendPauseCommand(webServerSessionSpy);
    sendUpdateCommand(webServerSessionSpy, 1);
    sendLineIssueCommand(webServerSessionSpy, 10001);
    sendUpdateCommand(webServerSessionSpy, 1);

    verify(webServerSessionSpy, times(2)).sendJson(messageCaptor.capture());
  }

  // Helper method to send the "pause" command
  private void sendPauseCommand(WebServerSession webServerSessionSpy) {
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "pause");
    webServerSessionSpy.onMessage(commandFromClient.toString());
  }

  // Helper method to send the "lineIssue" command
  private void sendLineIssueCommand(WebServerSession webServerSessionSpy, int lineId) {
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "lineIssue");
    commandFromClient.addProperty("id", lineId);
    webServerSessionSpy.onMessage(commandFromClient.toString());
  }

}
