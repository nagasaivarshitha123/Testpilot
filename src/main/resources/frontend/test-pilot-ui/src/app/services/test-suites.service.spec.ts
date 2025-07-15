import { TestBed } from '@angular/core/testing';

import { TestSuitesService } from './test-suites.service';

describe('TestSuitesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TestSuitesService = TestBed.get(TestSuitesService);
    expect(service).toBeTruthy();
  });
});
