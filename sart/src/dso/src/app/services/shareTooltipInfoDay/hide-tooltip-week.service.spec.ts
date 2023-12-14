import { TestBed } from '@angular/core/testing';

import { HideTooltipWeekService } from './hide-tooltip-week.service';

describe('HideTooltipWeekService', () => {
  let service: HideTooltipWeekService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HideTooltipWeekService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
