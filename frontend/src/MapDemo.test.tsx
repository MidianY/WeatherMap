import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom'
import MapDemo from './MapDemo'

test('Test nothing selected', () => {
    render(<MapDemo />);
    const areaDescription = screen.getByText(/city: Nothing is selected, state: Nothing is selected, name: Nothing is selecte./i);
    expect(areaDescription).toBeInTheDocument();
  });

test('Test map location is at expected position when application starts', () => {
    render(<MapDemo />);
    const areaDescription = screen.getByText(/lat=33.7457, long=-84.3110./i);
    expect(areaDescription).toBeInTheDocument();
  });

