import { TestBed } from '@angular/core/testing';

import { TooltipWeekPredictService } from './tooltip-week-predict.service';

describe('TooltipWeekPredictService', () => {
  let service: TooltipWeekPredictService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TooltipWeekPredictService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
