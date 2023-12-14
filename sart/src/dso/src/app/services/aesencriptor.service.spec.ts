import { TestBed } from '@angular/core/testing';

import { AESencriptorService } from './aesencriptor.service';

describe('AESencriptorService', () => {
  let service: AESencriptorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AESencriptorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
