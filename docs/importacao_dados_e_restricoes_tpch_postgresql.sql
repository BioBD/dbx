COPY nation FROM '/home/rpo/tpch/dbgen/nation.csv' WITH DELIMITER AS '|';
COPY customer FROM '/home/rpo/tpch/dbgen/customer.csv' WITH DELIMITER AS '|';
COPY part FROM '/home/rpo/tpch/dbgen/part.csv' WITH DELIMITER AS '|';
COPY region FROM '/home/rpo/tpch/dbgen/region.csv' WITH DELIMITER AS '|';
COPY lineitem FROM '/home/rpo/tpch/dbgen/lineitem.csv' WITH DELIMITER AS '|';
COPY orders FROM '/home/rpo/tpch/dbgen/orders.csv' WITH DELIMITER AS '|';
COPY partsupp FROM '/home/rpo/tpch/dbgen/partsupp.csv' WITH DELIMITER AS '|';
COPY supplier FROM '/home/rpo/tpch/dbgen/supplier.csv' WITH DELIMITER AS '|';

ALTER TABLE SUPPLIER ADD FOREIGN KEY (S_NATIONKEY) REFERENCES NATION(N_NATIONKEY);

ALTER TABLE PARTSUPP ADD FOREIGN KEY (PS_PARTKEY) REFERENCES PART(P_PARTKEY);

ALTER TABLE PARTSUPP ADD FOREIGN KEY (PS_SUPPKEY) REFERENCES SUPPLIER(S_SUPPKEY);

ALTER TABLE CUSTOMER ADD FOREIGN KEY (C_NATIONKEY) REFERENCES NATION(N_NATIONKEY);

ALTER TABLE ORDERS ADD FOREIGN KEY (O_CUSTKEY) REFERENCES CUSTOMER(C_CUSTKEY);

ALTER TABLE LINEITEM ADD FOREIGN KEY (L_ORDERKEY) REFERENCES ORDERS(O_ORDERKEY);

ALTER TABLE LINEITEM ADD FOREIGN KEY (L_PARTKEY,L_SUPPKEY) REFERENCES PARTSUPP(PS_PARTKEY,PS_SUPPKEY);

ALTER TABLE NATION ADD FOREIGN KEY (N_REGIONKEY) REFERENCES REGION(R_REGIONKEY);


CREATE INDEX l_shipdate_idx ON LINEITEM(L_SHIPDATE);