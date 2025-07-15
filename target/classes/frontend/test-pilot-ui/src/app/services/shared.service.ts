import { Injectable } from "@angular/core";
import { Subject, BehaviorSubject } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class SharedService {
  subject: Subject<Object> = new Subject<Object>();
  headerSubject: Subject<Object> = new Subject<Object>();
  filterSubject: Subject<Object> = new Subject<Object>();
  searchTextEmitterSubject: Subject<Object> = new Subject<Object>();
  callSearchTextSubject: Subject<Object> = new Subject<Object>();
  repositorySubject: Subject<Object> = new Subject<Object>();
  deleteSubject: BehaviorSubject<string> = new BehaviorSubject<string>("No");
  deleteSubject$ = this.deleteSubject.asObservable();
  existingTestCaseSubject: BehaviorSubject<Object> =
    new BehaviorSubject<Object>(null);
  existingTestCaseSubject$ = this.existingTestCaseSubject.asObservable();
  loadingSubject: BehaviorSubject<Object> = new BehaviorSubject<boolean>(false);
  loadingSubject$ = this.loadingSubject.asObservable();

  // TEST CASE PAGING SUBJECTS
  paginationSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(
    null
  );
  paginationSubject$ = this.paginationSubject.asObservable();

  previiousPageIndexSubject: BehaviorSubject<Object> =
    new BehaviorSubject<number>(null);
  previiousPageIndexSubject$ = this.previiousPageIndexSubject.asObservable();

  // TEST CASES REPORTS PAGING SUBJECTS
  filtersCaseSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(
    null
  );
  filtersCaseSubject$ = this.filtersCaseSubject.asObservable();

  // TEST SUITES PAGING SUBJECTS
  paginationSuitesSubject: BehaviorSubject<Object> =
    new BehaviorSubject<Object>(null);
  paginationSuitesSubject$ = this.paginationSuitesSubject.asObservable();

  previiousPageSuitesIndexSubject: BehaviorSubject<Object> =
    new BehaviorSubject<number>(null);
  previiousPageSuitesIndexSubject$ =
    this.previiousPageSuitesIndexSubject.asObservable();

  // TEST SUITES REPORTS PAGING SUBJECTS
  filtersSuitesSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(
    null
  );
  filtersSuitesSubject$ = this.filtersSuitesSubject.asObservable();

  //  REPOSITORY DATA
  repoSubjectData: BehaviorSubject<Object> = new BehaviorSubject<Object>(null);
  repoSubjectData$ = this.repoSubjectData.asObservable();

  // EDIT TESTCASE DATA
  editTestcaseSubjectData: BehaviorSubject<Object> =
    new BehaviorSubject<Object>(null);
  editTestcaseSubjectData$ = this.editTestcaseSubjectData.asObservable();

  // UPDATE REQUEST DATA
  updateRequestSubjectData: BehaviorSubject<Object> =
    new BehaviorSubject<Object>(null);
  updateRequestSubjectData$ = this.updateRequestSubjectData.asObservable();

  // UPDATE REQUEST DATA URL
  updateRequestURLSubjectData: BehaviorSubject<Object> =
    new BehaviorSubject<Object>(null);
  updateRequestURLSubjectData$ =
    this.updateRequestURLSubjectData.asObservable();

  // UPDATE REQUEST DATA URL
  updateRequestMethodSubjectData: BehaviorSubject<Object> =
    new BehaviorSubject<Object>(null);
  updateRequestMethodSubjectData$ =
    this.updateRequestMethodSubjectData.asObservable();

  constructor() {}
}
