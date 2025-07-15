--liquibase formatted sql
--changeset test-pilot:modify_columns_assertions_tables

ALTER TABLE test_case_assertions MODIFY COLUMN path LONGTEXT NULL;

ALTER TABLE test_case_assertions MODIFY COLUMN value LONGTEXT NULL;

ALTER TABLE test_case_assertions_response MODIFY COLUMN path LONGTEXT NULL;

ALTER TABLE test_case_assertions_response MODIFY COLUMN value LONGTEXT NULL;

ALTER TABLE test_case_assertions_response MODIFY COLUMN actual_value LONGTEXT NULL;