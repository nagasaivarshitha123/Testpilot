export interface DropDown {
  value: string;
  viewValue: string;
}

export interface createRepository {
  name: string;
  repositoryUrl: string;
  description: string;
  repositoryType: string;
  projectId: number;
}

export interface createRequest {
  name: string;
  requestUrl: string;
  description: string;
  requestType: string;
  projectId: number;
  requestId: number;
  requestMethod: string;
}

export interface requestProps {
  endPointUrl: string;
}

export interface runRequest {
  endpointUrl: string;
  method: string;
  requestBody?: any;
  requestHeaders?: any;
  requestAuthorization?: any;
}

export interface AuthType {
  authorization: {
    type: string;
    userName?: string;
    password?: string;
    token?: string;
  };
}

export interface CreateTestCaseType {
  description: string;
  name: string;
  projectId: string | number;
  requestId: string | number;
}

export interface getTestCaseParamsType {
  pageNo: number | string;
  pageSize: number | string;
  projectId: number | string;
  searchText: number | string;
}

export interface assertionsType {
  [id: string]: any;
  assertionType: string;
  path: string;
  comparison: string;
  value: string;
}

export interface filterTestCase {
  pageNo: string | number;
  pageSize: string | number;
  searchText: string;
  repositoryType: string;
  startDate: string;
  endDate: string;
  status: string;
}

export interface TestCaseFilter {
  pageNo: string | number;
  pageSize: string | number;
  searchText: string;
  projectId: number;
}

export interface TestSuiteCreate {
  name: string;
  description: string;
  projectId: number | string;
  testCases: any;
}

export interface metaData {
  status: number | string;
  time: number | string;
  size: number | string;
}
