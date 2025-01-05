package edu.umn.cs.csci3081w.project.model;

public class BusFactory implements VehicleFactory {
  private GenerationStrategy generationStrategy;
  private Counter counter;
  private StorageFacility storageFacility;

  /**
   * Constructor for bus factory that selects strategy based on time.
   *
   * @param storageFacility storage facility information
   * @param counter         counter information
   * @param time            time used to select strategy
   */
  public BusFactory(StorageFacility storageFacility, Counter counter, int time) {
    if (time >= 8 && time < 18) {
      generationStrategy = new BusStrategyDay();
    } else {
      generationStrategy = new BusStrategyNight();
    }
    this.storageFacility = storageFacility;
    this.counter = counter;
  }

  @Override
  public Vehicle generateVehicle(Line line) {
    String typeOfVehicle = generationStrategy.getTypeOfVehicle(storageFacility);
    Vehicle generatedVehicle = null;
    if (typeOfVehicle != null && typeOfVehicle.equals(SmallBus.SMALL_BUS_VEHICLE)) {
      generatedVehicle = new SmallBus(counter.getSmallBusIdCounterAndIncrement(),
          line, SmallBus.CAPACITY, SmallBus.SPEED);
      generatedVehicle = new MaroonDecorator(generatedVehicle);
      generatedVehicle = new AlphaDecorator(generatedVehicle);
      storageFacility.decrementSmallBusesNum();
    } else if (typeOfVehicle != null && typeOfVehicle.equals(LargeBus.LARGE_BUS_VEHICLE)) {
      generatedVehicle = new LargeBus(counter.getLargeBusIdCounterAndIncrement(), line,
          LargeBus.CAPACITY, LargeBus.SPEED);
      generatedVehicle = new PinkDecorator(generatedVehicle);
      generatedVehicle = new AlphaDecorator(generatedVehicle);
      storageFacility.decrementLargeBusesNum();
    }
    return generatedVehicle;
  }

  @Override
  public void returnVehicle(Vehicle vehicle) {
    if (vehicle.getVehicleType().equals(SmallBus.SMALL_BUS_VEHICLE)) {
      storageFacility.incrementSmallBusesNum();
    } else if (vehicle.getVehicleType().equals(LargeBus.LARGE_BUS_VEHICLE)) {
      storageFacility.incrementLargeBusesNum();
    }
  }

  /**
   * Get storage facility.
   *
   * @return storage facility
   */
  public StorageFacility getStorageFacility() {
    return storageFacility;
  }

  /**
   * Get generation strategy.
   *
   * @return generation strategy
   */
  public GenerationStrategy getGenerationStrategy() {
    return generationStrategy;
  }

}
