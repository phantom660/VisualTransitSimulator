package edu.umn.cs.csci3081w.project.webserver;

import com.google.gson.JsonObject;

public abstract class SimulatorCommand {

  /**
   * Executes a simulator command.
   *
   * @param session current session
   * @param command command to be executed
   */
  public abstract void execute(WebServerSession session, JsonObject command);
}
