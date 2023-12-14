import { TestBed } from '@angular/core/testing';

import { HideTooltipStatisiticPotService } from './hide-tooltip-statisitic-pot.service';

describe('HideTooltipStatisiticPotService', () => {
  let service: HideTooltipStatisiticPotService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HideTooltipStatisiticPotService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
