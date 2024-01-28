INSERT INTO endpoints
SELECT *
FROM (
         VALUES
             ROW(1,'/admin/statistic'),
             ROW(2,'/admin/cash-register'),
             ROW(3,'/admin/invoices'),
             ROW(4,'/admin/personal-accounts'),
             ROW(5,'/admin/apartments'),
             ROW(6,'/admin/owners'),
             ROW(7,'/admin/houses'),
             ROW(8,'/admin/messages'),
             ROW(9,'/admin/master-requests'),
             ROW(10,'/admin/meter-readings'),
             ROW(11,'/admin/site-management'),
             ROW(12,'/admin/system-settings/services'),
             ROW(13,'/admin/system-settings/tariffs'),
             ROW(14,'/admin/system-settings/roles'),
             ROW(15,'/admin/system-settings/staff'),
             ROW(16,'/admin/system-settings/payment-details'),
             ROW(17,'/admin/system-settings/payment-items')
     ) source_data
WHERE NOT EXISTS (
    SELECT NULL
    FROM endpoints
);

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

INSERT INTO permissions
SELECT *
FROM (
         VALUES
             ROW(true,1,1,1),
             ROW(true,2,2,1),
             ROW(true,3,3,1),
             ROW(true,4,4,1),
             ROW(true,5,5,1),
             ROW(true,6,6,1),
             ROW(true,7,7,1),
             ROW(true,8,8,1),
             ROW(true,9,9,1),
             ROW(true,10,10,1),
             ROW(true,11,11,1),
             ROW(true,12,12,1),
             ROW(true,13,13,1),
             ROW(true,14,14,1),
             ROW(true,15,15,1),
             ROW(true,16,16,1),
             ROW(true,17,17,1),
             ROW(true,1,18,2),
             ROW(true,2,19,2),
             ROW(true,3,20,2),
             ROW(true,4,21,2),
             ROW(true,5,22,2),
             ROW(true,6,23,2),
             ROW(true,7,24,2),
             ROW(true,8,25,2),
             ROW(true,9,26,2),
             ROW(true,10,27,2),
             ROW(true,11,28,2),
             ROW(true,12,29,2),
             ROW(true,13,30,2),
             ROW(false,14,31,2),
             ROW(true,15,32,2),
             ROW(true,16,33,2),
             ROW(true,17,34,2),
             ROW(true,1,35,3),
             ROW(true,2,36,3),
             ROW(true,3,37,3),
             ROW(false,4,38,3),
             ROW(false,5,39,3),
             ROW(false,6,40,3),
             ROW(false,7,41,3),
             ROW(true,8,42,3),
             ROW(false,9,43,3),
             ROW(false,10,44,3),
             ROW(false,11,45,3),
             ROW(false,12,46,3),
             ROW(false,13,47,3),
             ROW(false,14,48,3),
             ROW(false,15,49,3),
             ROW(true,16,50,3),
             ROW(true,17,51,3),
             ROW(true,1,52,4),
             ROW(false,2,53,4),
             ROW(false,3,54,4),
             ROW(false,4,55,4),
             ROW(false,5,56,4),
             ROW(false,6,57,4),
             ROW(false,7,58,4),
             ROW(true,8,59,4),
             ROW(true,9,60,4),
             ROW(false,10,61,4),
             ROW(false,11,62,4),
             ROW(false,12,63,4),
             ROW(false,13,64,4),
             ROW(false,14,65,4),
             ROW(false,15,66,4),
             ROW(false,16,67,4),
             ROW(false,17,68,4),
             ROW(true,1,69,5),
             ROW(false,2,70,5),
             ROW(false,3,71,5),
             ROW(false,4,72,5),
             ROW(false,5,73,5),
             ROW(false,6,74,5),
             ROW(false,7,75,5),
             ROW(true,8,76,5),
             ROW(true,9,77,5),
             ROW(false,10,78,5),
             ROW(false,11,79,5),
             ROW(false,12,80,5),
             ROW(false,13,81,5),
             ROW(false,14,82,5),
             ROW(false,15,83,5),
             ROW(false,16,84,5),
             ROW(false,17,85,5)
     ) source_data
WHERE NOT EXISTS (
    SELECT NULL
    FROM permissions
);