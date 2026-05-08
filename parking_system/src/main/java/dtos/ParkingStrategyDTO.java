package dtos;

import enums.DecoratorType;
import factories.DecoratorFactoryConfig;

/*
 * DTO for parking strategy configuration. This class encapsulates the decorator type and its associated configuration parameters.
 */
public class ParkingStrategyDTO {
	
	DecoratorType decoratorType;
	
	DecoratorFactoryConfig decoratorConfig;

	public DecoratorType getDecoratorType() {
		return decoratorType;
	}

	public void setDecoratorType(DecoratorType decoratorType) {
		this.decoratorType = decoratorType;
	}

	public DecoratorFactoryConfig getDecoratorConfig() {
		return decoratorConfig;
	}

	public void setDecoratorConfig(DecoratorFactoryConfig decoratorConfig) {
		this.decoratorConfig = decoratorConfig;
	}

}
