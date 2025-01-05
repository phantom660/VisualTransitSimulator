package edu.umn.cs.csci3081w.project.model;

import java.awt.Color;

public class PinkDecorator extends VehicleDecorator {
  public PinkDecorator(Vehicle vehicle) {
    super(vehicle);
  }

  @Override
  public Color getColor() {
    Color color = vehicle.getColor();
    return new Color(239, 130, 238, color.getAlpha());
  }
}
