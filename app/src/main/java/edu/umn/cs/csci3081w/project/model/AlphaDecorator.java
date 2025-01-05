package edu.umn.cs.csci3081w.project.model;

import java.awt.Color;

/**
 * Class for the Transparent Color.
 */
public class AlphaDecorator extends VehicleDecorator {

  /**
   * Constructor for AlphaDecorator.
   *
   * @param vehicle current vehicle
   */
  public AlphaDecorator(Vehicle vehicle) {
    super(vehicle);
  }

  @Override
  public Color getColor() {
    Color currentColor = vehicle.getColor();
    int alphaVal = vehicle.getLine().isIssueExist() ? 155 : 255;
    return new Color(currentColor.getRed(), currentColor.getGreen(),
        currentColor.getBlue(), alphaVal);
  }
}
