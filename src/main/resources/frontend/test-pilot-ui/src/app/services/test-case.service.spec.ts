import { TestBed } from '@angular/core/testing';

import { TestCaseService } from './test-case.service';

describe('TestCaseService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TestCaseService = TestBed.get(TestCaseService);
    expect(service).toBeTruthy();
  });
});
