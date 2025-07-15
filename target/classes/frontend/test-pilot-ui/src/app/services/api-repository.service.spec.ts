import { TestBed } from '@angular/core/testing';

import { ApiRepositoryService } from './api-repository.service';

describe('ApiRepositoryService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ApiRepositoryService = TestBed.get(ApiRepositoryService);
    expect(service).toBeTruthy();
  });
});
