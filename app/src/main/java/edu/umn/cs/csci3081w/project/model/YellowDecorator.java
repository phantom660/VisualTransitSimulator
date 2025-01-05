package edu.umn.cs.csci3081w.project.model;

import java.awt.Color;

public class YellowDecorator extends VehicleDecorator {
  public YellowDecorator(Vehicle vehicle) {
    super(vehicle);
  }

  @Override
  public Color getColor() {
    Color color = vehicle.getColor();
    return new Color(255, 204, 51, color.getAlpha());
  }
}
