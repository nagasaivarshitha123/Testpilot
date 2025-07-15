export const navbarData = [
  {
    routerlink: "apirepository",
    icon: "fa-solid fa-box",
    label: "Api Repository",
    expanded: true,
    items: [],
    level: true,
  },
  {
    routerlink: "testcases/manage",
    icon: "fa-solid fa-list-check",
    label: "Test Cases",
    items: [
      {
        routerlink: "testcases/manage",
        label: "Manage",
        icon: "fa-solid fa-suitcase",
      },
      {
        routerlink: "testcases/reports",
        label: "Reports",
        icon: "fa-solid fa-chart-simple",
      },
    ],
  },
  {
    routerlink: "testsuites/manage",
    icon: "fa-solid fa-table-list",
    label: "Test Suites",
    items: [
      {
        routerlink: "testsuites/manage",
        label: "Manage",
        icon: "fa-solid fa-suitcase",
      },
      {
        routerlink: "testsuites/reports",
        label: "Reports",
        icon: "fa-solid fa-chart-simple",
      },
    ],
  },
];
