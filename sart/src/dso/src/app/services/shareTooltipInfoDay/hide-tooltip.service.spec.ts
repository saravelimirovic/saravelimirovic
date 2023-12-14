import { TestBed } from '@angular/core/testing';

import { HideTooltipService } from './hide-tooltip.service';

describe('HideTooltipService', () => {
  let service: HideTooltipService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HideTooltipService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
