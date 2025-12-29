package com.back.global.config.init;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SectorProfile {
    TECHNOLOGY("Technology", 500_000_000, 0.15, 0.25, 0.4, 1.5, 0.15, 0.05, 30.0, 0.40),
    FINANCIAL_SERVICES("Financial Services", 800_000_000, 0.05, 0.30, 0.85, 5.0, 0.01, 0.02, 12.0, 0.20),
    HEALTHCARE("Healthcare", 400_000_000, 0.10, 0.15, 0.5, 1.2, 0.20, 0.05, 25.0, 0.30),
    CONSUMER_CYCLICAL("Consumer Cyclical", 300_000_000, 0.08, 0.10, 0.6, 1.0, 0.03, 0.05, 18.0, 0.35),
    CONSUMER_DEFENSIVE("Consumer Defensive", 600_000_000, 0.03, 0.08, 0.5, 1.0, 0.01, 0.03, 15.0, 0.15),
    INDUSTRIAL("Industrial", 450_000_000, 0.06, 0.12, 0.6, 1.8, 0.02, 0.10, 16.0, 0.25),
    BASIC_MATERIALS("Basic Materials", 400_000_000, 0.04, 0.15, 0.5, 2.0, 0.01, 0.15, 14.0, 0.30),
    DEFAULT("Default", 300_000_000, 0.05, 0.10, 0.5, 1.0, 0.05, 0.05, 20.0, 0.25);

    private final String sectorName;
    private final double baseRevenue;
    private final double growthRate;
    private final double operatingMargin;
    private final double debtRatio;
    private final double assetTurnoverMultiplier;
    private final double rndRatio;
    private final double capexRatio;
    private final double peRatio;
    private final double volatility;

    public static SectorProfile findBySector(String sectorName) {
        return Arrays.stream(values())
                .filter(profile -> profile.sectorName.equalsIgnoreCase(sectorName))
                .findFirst()
                .orElse(DEFAULT);
    }
}