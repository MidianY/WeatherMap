import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom'
import App from './App';

test('renders instructions', () => {
  render(<App />);
  // The "i" modifier means a case-insensitive match
  const instructionElement = screen.getByText(/Redlining Data in the United State./i);
  expect(instructionElement).toBeInTheDocument();
});
