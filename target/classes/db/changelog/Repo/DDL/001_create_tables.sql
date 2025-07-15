--liquibase formatted sql
--changeset test-pilot:create-tables
CREATE TABLE `users` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`email` varchar(255) NOT NULL,
	`otp` varchar(10) DEFAULT NULL,
	`otp_generated_at` datetime(6)	DEFAULT NULL,
	`is_verified` tinyint(1) NOT NULL DEFAULT '0',
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
	`creation_date` datetime(6)	DEFAULT NULL,
	`updation_date` datetime(6) DEFAULT NULL,
	`created_by` varchar(255) DEFAULT NULL,
	`updated_by` int DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE (email)
);

CREATE TABLE `projects` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`description` varchar(1024) NOT NULL,
	`project_type` varchar(255) NOT NULL,
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
	`creation_date` datetime(6)	DEFAULT NULL,
	`updation_date` datetime(6) DEFAULT NULL,
	`created_by` int DEFAULT NULL,
	`updated_by` int DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE (name)
);

CREATE TABLE `user_projects` (
	`id` int NOT NULL AUTO_INCREMENT,
	`user_id` INT NOT NULL,
	`project_id` INT NOT NULL,
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
    `creation_date` datetime(6)	DEFAULT NULL,
    `updation_date` datetime(6) DEFAULT NULL,
    `created_by` int DEFAULT NULL,
    `updated_by` int DEFAULT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
	FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
);

CREATE TABLE `api_repository` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`description` varchar(1024) NOT NULL,
	`project_id` INT NOT NULL,
	`repository_url` varchar(1024) NOT NULL,
	`repository_type` varchar(255) NOT NULL,
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
	`creation_date` datetime(6)	DEFAULT NULL,
	`updation_date` datetime(6) DEFAULT NULL,
	`created_by` int DEFAULT NULL,
	`updated_by` int DEFAULT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `request` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`description` varchar(1024) NOT NULL,
	`method` varchar(100) NOT NULL,
	`project_id` INT NOT NULL,
	`repository_id` INT NOT NULL,
	`endpoint_url` varchar(1024) NOT NULL,
	`authorization` json DEFAULT NULL,
	`headers` json DEFAULT NULL,
	`body` json DEFAULT NULL,
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
	`creation_date` datetime(6)	DEFAULT NULL,
	`updation_date` datetime(6) DEFAULT NULL,
	`created_by` int DEFAULT NULL,
	`updated_by` int DEFAULT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`repository_id`) REFERENCES `api_repository` (`id`)
);

CREATE TABLE `testcase_master` (
	`testcase_id` int NOT NULL AUTO_INCREMENT,
	`testcase_name` varchar(255) NOT NULL,
	`description` varchar(1024) NOT NULL,
	`project_id` INT NOT NULL,
	`request_id` INT NOT NULL,
	`endpoint_url` varchar(1024) NOT NULL,
	`method` varchar(100) NOT NULL,
	`authorization` json DEFAULT NULL,
    `headers` json DEFAULT NULL,
    `body` json DEFAULT NULL,
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
	`creation_date` datetime(6)	DEFAULT NULL,
    `updation_date` datetime(6) DEFAULT NULL,
    `created_by` int DEFAULT NULL,
    `updated_by` int DEFAULT NULL,
	PRIMARY KEY (`testcase_id`),
	FOREIGN KEY (`request_id`) REFERENCES `request` (`id`)
);

CREATE TABLE `test_case_assertions` (
	`id` int NOT NULL AUTO_INCREMENT,
	`testcase_id` INT NOT NULL,
	`assertion_type` varchar(255) DEFAULT NULL,
	`path` varchar(255) DEFAULT NULL,
	`comparison` varchar(255) DEFAULT NULL,
	`value` varchar(255) DEFAULT NULL,
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
    `creation_date` datetime(6)	DEFAULT NULL,
    `updation_date` datetime(6) DEFAULT NULL,
    `created_by` int DEFAULT NULL,
    `updated_by` int DEFAULT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`testcase_id`) REFERENCES `testcase_master` (`testcase_id`)
);

CREATE TABLE `testcase_reports` (
	`testcase_run_id` int NOT NULL AUTO_INCREMENT,
	`project_id` INT NOT NULL,
	`testcase_id` INT NOT NULL,
	`endpoint_url` varchar(1024) NOT NULL,
	`method` varchar(100) NOT NULL,
	`request_authorization` json DEFAULT NULL,
	`request_headers` json DEFAULT NULL,
	`request_body` json DEFAULT NULL,
	`response_headers` json DEFAULT NULL,
	`response_body` LONGTEXT DEFAULT NULL,
	`response_metadata` json DEFAULT NULL,
	`execution_date` datetime(6) DEFAULT NULL,
	`testsuite_report_id` INT DEFAULT NULL,
	`total_assertions` int DEFAULT NULL,
	`passed_assertions` int DEFAULT NULL,
	`failed_assertions` int DEFAULT NULL,
	`status` varchar(255) DEFAULT NULL,
	PRIMARY KEY (`testcase_run_id`),
	FOREIGN KEY (`testcase_id`) REFERENCES `testcase_master` (`testcase_id`)
);

CREATE TABLE `test_case_assertions_response` (
	`id` int NOT NULL AUTO_INCREMENT,
	`testcase_run_id` INT NOT NULL,
	`assertion_type` varchar(255) DEFAULT NULL,
	`path` varchar(255) DEFAULT NULL,
	`comparison` varchar(255) DEFAULT NULL,
	`value` varchar(255) DEFAULT NULL,
	`actual_value` varchar(255) DEFAULT NULL,
	`status` varchar(255) DEFAULT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`testcase_run_id`) REFERENCES `testcase_reports` (`testcase_run_id`)
);

CREATE TABLE `testsuite_master` (
	`testsuite_id` int NOT NULL AUTO_INCREMENT,
	`testsuite_name` varchar(255) NOT NULL,
	`description` varchar(1024) NOT NULL,
	`project_id` INT NOT NULL,
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
	`creation_date` datetime(6)	DEFAULT NULL,
    `updation_date` datetime(6) DEFAULT NULL,
    `created_by` int DEFAULT NULL,
    `updated_by` int DEFAULT NULL,
	PRIMARY KEY (`testsuite_id`)
);

CREATE TABLE `test_suite_test_cases` (
	`id` int NOT NULL AUTO_INCREMENT,
	`testsuite_id` INT NOT NULL,
	`testcase_id` INT NOT NULL,
	`enabled` tinyint(1) NOT NULL DEFAULT '1',
	`active_status` tinyint(1) NOT NULL DEFAULT '1',
    `creation_date` datetime(6)	DEFAULT NULL,
    `updation_date` datetime(6) DEFAULT NULL,
    `created_by` int DEFAULT NULL,
    `updated_by` int DEFAULT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`testsuite_id`) REFERENCES `testsuite_master` (`testsuite_id`),
	FOREIGN KEY (`testcase_id`) REFERENCES `testcase_master` (`testcase_id`)
);

CREATE TABLE `testsuite_reports_master` (
	`testsuite_run_id` int NOT NULL AUTO_INCREMENT,
	`project_id` INT NOT NULL,
	`testsuite_id` INT NOT NULL,
	`execution_date` datetime(6) DEFAULT NULL,
	`enddate` datetime(6) DEFAULT NULL,
	`total_test_cases` int DEFAULT NULL,
	`passed_test_cases` int DEFAULT NULL,
	`failed_test_cases` int DEFAULT NULL,
	`status` varchar(255) DEFAULT NULL,
	PRIMARY KEY (`testsuite_run_id`),
	FOREIGN KEY (`testsuite_id`) REFERENCES `testsuite_master` (`testsuite_id`)
);

ALTER TABLE `testcase_reports` ADD FOREIGN KEY (`testsuite_report_id`) REFERENCES `testsuite_reports_master` (`testsuite_run_id`);