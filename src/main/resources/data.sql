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
