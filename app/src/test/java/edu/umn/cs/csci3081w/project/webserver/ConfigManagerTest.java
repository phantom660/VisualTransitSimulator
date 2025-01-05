package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umn.cs.csci3081w.project.model.Counter;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.StorageFacility;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ConfigManager} class.
 * Verifies the functionality of configuration reading and validation of
 * generated objects such as {@link Line} and {@link StorageFacility}.
 */
public class ConfigManagerTest {

  private ConfigManager configManager;

  @BeforeEach
  public void setUp() {
    configManager = new ConfigManager();
  }

  /**
   * Verifies that the configuration file is read correctly.
   *
   * @throws UnsupportedEncodingException if the file path cannot be properly decoded.
   */
  @Test
  public void testReadConfigFile() throws UnsupportedEncodingException {
    // Act: Read the configuration file
    readConfigFile("config.txt");

    // Assert: Ensure that the configuration file was read correctly
    assertNotNull(configManager.getLines(),
        "The lines should be successfully read from the config file.");
  }

  /**
   * Verifies that the number of lines read from the configuration file is as expected.
   *
   * @throws UnsupportedEncodingException if the file path cannot be properly decoded.
   */
  @Test
  public void testValidateNumberOfLines() throws UnsupportedEncodingException {
    // Act: Read the configuration file
    readConfigFile("config.txt");

    // Assert: Ensure the number of lines is 2
    List<Line> lines = configManager.getLines();
    assertEquals(2, lines.size(), "The number of lines read from the configuration should be 2.");
  }

  /**
   * Helper method to read the configuration file from the resources.
   */
  private void readConfigFile(String fileName) throws UnsupportedEncodingException {
    configManager.readConfig(new Counter(), URLDecoder.decode(
        getClass().getClassLoader().getResource(fileName).getFile(), "UTF-8"));
  }
}
