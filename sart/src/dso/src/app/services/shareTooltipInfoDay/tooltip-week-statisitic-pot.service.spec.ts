import { TestBed } from '@angular/core/testing';

import { TooltipWeekStatisiticPotService } from './tooltip-week-statisitic-pot.service';

describe('TooltipWeekStatisiticPotService', () => {
  let service: TooltipWeekStatisiticPotService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TooltipWeekStatisiticPotService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
