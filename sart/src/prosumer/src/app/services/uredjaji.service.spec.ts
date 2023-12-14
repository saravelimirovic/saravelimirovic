import { TestBed } from '@angular/core/testing';

import { UredjajiService } from './uredjaji.service';

describe('UredjajiService', () => {
  let service: UredjajiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UredjajiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
