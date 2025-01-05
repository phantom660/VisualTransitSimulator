package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import edu.umn.cs.csci3081w.project.model.Issue;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.webserver.VisualTransitSimulator;
import edu.umn.cs.csci3081w.project.webserver.WebServerSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import javax.websocket.Session;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link VisualTransitSimulator} class.
 * Verifies the functionality of simulator operations such as initialization,
 * vehicle factory settings, and updates.
 */
public class VisualTransitSimulatorTest {

  // Helper method to initialize the simulator
  private VisualTransitSimulator initializeSimulator(
      boolean loggingEnabled) throws UnsupportedEncodingException {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    Session session = mock(Session.class);
    webServerSessionSpy.onOpen(session);

    // Set the logging flag
    VisualTransitSimulator.LOGGING = loggingEnabled;

    return new VisualTransitSimulator(
        URLDecoder.decode(
            getClass().getClassLoader().getResource("config.txt").getFile(),
            "UTF-8"
        ),
        webServerSessionSpy
    );
  }

  /**
   * Verifies the behavior when incorrect line type is set, issues are created,
   * and the simulator updates. Tests both with and without logging enabled.
   *
   * @throws UnsupportedEncodingException if the file path cannot be properly decoded.
   */
  @Test
  public void testEdges() throws UnsupportedEncodingException {
    // Test without logging (loggingEnabled = false)
    runEdgeTest(false);

    // Test with logging enabled (loggingEnabled = true)
    runEdgeTest(true);
  }

  /**
   * Helper method to run the edge test with the specified logging flag.
   */
  private void runEdgeTest(boolean loggingEnabled) throws UnsupportedEncodingException {
    VisualTransitSimulator simulator = initializeSimulator(loggingEnabled);


    simulator.start(Arrays.asList(3, 3), 5);
    simulator.setVehicleFactories(4);

    Line line = simulator.getLines().getFirst();
    simulator.getLines().set(0, createInvalidLine(line));

    updateSimulator(simulator, 5);


    assertTrue(simulator.getActiveVehicles().size() < 5);

    line.createIssue();
    simulator.getLines().set(0, line);
    simulator.update();

    assertTrue(simulator.getActiveVehicles().size() < 5);
  }

  // Helper method to create an invalid line with a wrong type
  private Line createInvalidLine(Line line) {
    return new Line(line.getId(), line.getName(), "invalid",
        line.getOutboundRoute(), line.getInboundRoute(), new Issue());
  }

  // Helper method to perform updates on the simulator multiple times
  private void updateSimulator(VisualTransitSimulator simulator, int times) {
    for (int i = 0; i < times; i++) {
      simulator.update();
    }
  }
}