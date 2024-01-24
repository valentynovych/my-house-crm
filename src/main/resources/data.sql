INSERT INTO roles
SELECT *
FROM (
         VALUES
             ROW(1,'DIRECTOR'),
             ROW(2,'MANAGER'),
             ROW(3,'ACCOUNTANT'),
             ROW(4,'ELECTRICIAN'),
             ROW(5,'PLUMBER')
     ) source_data
WHERE NOT EXISTS (
    SELECT NULL
    FROM roles
);