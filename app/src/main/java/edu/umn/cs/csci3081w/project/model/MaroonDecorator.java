package edu.umn.cs.csci3081w.project.model;

import java.awt.Color;

public class MaroonDecorator extends VehicleDecorator {
  public MaroonDecorator(Vehicle vehicle) {
    super(vehicle);
  }

  @Override
  public Color getColor() {
    Color color = vehicle.getColor();
    return new Color(122, 0, 25, color.getAlpha());
  }
}
