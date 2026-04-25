package dtos;

import enums.StrategyType;
import factories.StrategyFactoryConfig;

/*
 * DTO for parking strategy configuration. This class encapsulates the strategy type and its associated configuration parameters.
 */
public class ParkingStrategyDTO {
	
	StrategyType strategyType;
	
	StrategyFactoryConfig strategyConfig;

	public StrategyType getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(StrategyType strategyType) {
		this.strategyType = strategyType;
	}

	public StrategyFactoryConfig getStrategyConfig() {
		return strategyConfig;
	}

	public void setStrategyConfig(StrategyFactoryConfig strategyConfig) {
		this.strategyConfig = strategyConfig;
	}

}