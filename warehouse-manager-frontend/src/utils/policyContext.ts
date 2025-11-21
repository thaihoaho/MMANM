// Policy context utility for Zero Trust Architecture
export interface PolicyContext {
  location: {
    lat: number;
    lon: number;
  };
  localTime: string; // ISO string
  risk: number; // number from 0 to 1
}

// Function to get policy context (location, time, risk)
export const getPolicyContext = (): PolicyContext => {
  // In a real application, you might get the actual location from browser API or other sources
  // For now, we'll use mock values
  return {
    location: {
      lat: 21.0278, // Hanoi coordinates (mock)
      lon: 105.8342 // Hanoi coordinates (mock)
    },
    localTime: new Date().toISOString(),
    risk: Math.random() // Random risk value between 0 and 1 (in real app, this would be calculated based on various factors)
  };
};