package edu.umn.cs.csci3081w.project.model;

public interface VehicleFactory {

  /**
   * Generates vehicle for a line.
   *
   * @param line line for which vehicle is generated
   * @return vehicle generated
   */
  public Vehicle generateVehicle(Line line);

  /**
   * Returns the vehicle.
   *
   * @param vehicle vehicle to be returned
   */
  public void returnVehicle(Vehicle vehicle);
}
