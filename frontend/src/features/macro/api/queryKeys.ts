export const macroKeys = {
  all: ["macro"] as const,
  history: () => [...macroKeys.all, "history"] as const,
  indicators: () => [...macroKeys.all, "indicators"] as const,
};