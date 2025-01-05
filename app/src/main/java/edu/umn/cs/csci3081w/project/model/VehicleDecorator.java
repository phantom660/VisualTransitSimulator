package edu.umn.cs.csci3081w.project.model;

import com.google.gson.JsonObject;
import java.awt.Color;
import java.io.PrintStream;
import java.util.List;

public abstract class VehicleDecorator extends Vehicle {
  protected Vehicle vehicle;

  /**
   * Constructor for VehicleDecorator.
   *
   * @param vehicle vehicle that has to be colored
   */
  public VehicleDecorator(Vehicle vehicle) {
    super(vehicle.getId(), vehicle.getLine(), vehicle.getCapacity(),
        vehicle.getSpeed(), vehicle.getPassengerLoader(),
        vehicle.getPassengerUnloader());
    this.vehicle = vehicle;
  }

  @Override
  public void report(PrintStream out) {
    vehicle.report(out);
  }

  @Override
  public int getCurrentCO2Emission() {
    return vehicle.getCurrentCO2Emission();
  }

  @Override
  public int getId() {
    return vehicle.getId();
  }

  @Override
  public int getCapacity() {
    return vehicle.getCapacity();
  }

  @Override
  public double getSpeed() {
    return vehicle.getSpeed();
  }

  @Override
  public PassengerLoader getPassengerLoader() {
    return vehicle.getPassengerLoader();
  }

  @Override
  public PassengerUnloader getPassengerUnloader() {
    return vehicle.getPassengerUnloader();
  }

  @Override
  public List<Passenger> getPassengers() {
    return vehicle.getPassengers();
  }

  @Override
  public String getName() {
    return vehicle.getName();
  }

  @Override
  public Position getPosition() {
    return vehicle.getPosition();
  }

  @Override
  public boolean isTripComplete() {
    return vehicle.isTripComplete();
  }

  @Override
  public int loadPassenger(Passenger newPassenger) {
    return vehicle.loadPassenger(newPassenger);
  }

  @Override
  public void move() {
    vehicle.move();
  }

  @Override
  public void update() {
    vehicle.update();
  }

  @Override
  public Stop getNextStop() {
    return vehicle.getNextStop();
  }

  @Override
  public Line getLine() {
    return vehicle.getLine();
  }

  @Override
  public double getDistanceRemaining() {
    return vehicle.getDistanceRemaining();
  }

  @Override
  public String getVehicleType() {
    return vehicle.getVehicleType();
  }

  @Override
  public Color getColor() {
    return vehicle.getColor();
  }

  @Override
  public boolean provideInfo() {
    return vehicle.provideInfo();
  }

  @Override
  public void setVehicleSubject(VehicleConcreteSubject
                                    vehicleConcreteSubject) {
    vehicle.setVehicleSubject(vehicleConcreteSubject);
  }
}
