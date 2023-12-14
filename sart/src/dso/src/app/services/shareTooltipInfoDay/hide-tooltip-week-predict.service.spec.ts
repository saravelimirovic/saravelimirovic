import { TestBed } from '@angular/core/testing';

import { HideTooltipWeekPredictService } from './hide-tooltip-week-predict.service';

describe('HideTooltipWeekPredictService', () => {
  let service: HideTooltipWeekPredictService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HideTooltipWeekPredictService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
