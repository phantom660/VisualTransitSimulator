package edu.umn.cs.csci3081w.project.model;

import java.awt.Color;

public class GreenDecorator extends VehicleDecorator {
  public GreenDecorator(Vehicle vehicle) {
    super(vehicle);
  }

  @Override
  public Color getColor() {
    Color color = vehicle.getColor();
    return new Color(60, 179, 113, color.getAlpha());
  }
}
