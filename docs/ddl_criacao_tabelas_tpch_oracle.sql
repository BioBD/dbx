drop user tpch cascade;
CREATE USER tpch IDENTIFIED BY gagasenha;

GRANT RESOURCE TO tpch;
GRANT CREATE PROCEDURE TO tpch;
GRANT CREATE PUBLIC SYNONYM TO tpch;
GRANT DROP PUBLIC SYNONYM TO tpch;
GRANT CREATE SEQUENCE TO tpch;
GRANT CREATE SESSION TO tpch;
GRANT CREATE SYNONYM TO tpch;
GRANT CREATE TABLE TO tpch;
GRANT EXECUTE ANY PROCEDURE TO tpch;
GRANT UNLIMITED TABLESPACE TO tpch;
grant select any dictionary to tpch;


-----
-- Table: TPCH.H_CUSTOMER
-----
DROP TABLE tpch.h_customer PURGE;
CREATE TABLE tpch.h_customer (
     c_custkey    NUMBER NOT NULL
    ,c_name       VARCHAR2(25)
    ,c_address    VARCHAR2(40)
    ,c_nationkey  NUMBER
    ,c_phone      CHAR(15)
    ,c_acctbal    NUMBER
    ,c_mktsegment CHAR(10)
    ,c_comment    VARCHAR2(117)
);

-----
-- Table: TPCH.H_LINEITEM
-----
DROP TABLE tpch.h_lineitem PURGE;
CREATE TABLE tpch.h_lineitem (
     l_orderkey      NUMBER NOT NULL 
    ,l_partkey       NUMBER NOT NULL 
    ,l_suppkey       NUMBER NOT NULL 
    ,l_linenumber    NUMBER NOT NULL 
    ,l_quantity      NUMBER NOT NULL 
    ,l_extendedprice NUMBER NOT NULL 
    ,l_discount      NUMBER NOT NULL 
    ,l_tax           NUMBER NOT NULL 
    ,l_returnflag    CHAR(1) 
    ,l_linestatus    CHAR(1) 
    ,l_shipdate      DATE 
    ,l_commitdate    DATE 
    ,l_receiptdate   DATE 
    ,l_shipinstruct  CHAR(25) 
    ,l_shipmode      CHAR(10) 
    ,l_comment       VARCHAR2(44)
);

-----
-- Table: TPCH.H_NATION
-----
DROP TABLE tpch.h_nation PURGE;
CREATE TABLE tpch.h_nation (
     n_nationkey NUMBER NOT NULL 
    ,n_name      CHAR(25) 
    ,n_regionkey NUMBER 
    ,n_comment   VARCHAR2(152)
);

-----
-- Table: TPCH.H_ORDER
-----
DROP TABLE tpch.h_order PURGE;
CREATE TABLE tpch.h_order (
     o_orderkey      NUMBER NOT NULL 
    ,o_custkey       NUMBER NOT NULL 
    ,o_orderstatus   CHAR(1) 
    ,o_totalprice    NUMBER 
    ,o_orderdate     DATE 
    ,o_orderpriority CHAR(15) 
    ,o_clerk         CHAR(15) 
    ,o_shippriority  NUMBER 
    ,o_comment       VARCHAR2(79)
);

-----
-- Table: TPCH.H_PART
-----
DROP TABLE tpch.h_part PURGE;
CREATE TABLE tpch.h_part (
     p_partkey     NUMBER NOT NULL 
    ,p_name        VARCHAR2(55) 
    ,p_mfgr        CHAR(25) 
    ,p_brand       CHAR(10) 
    ,p_type        VARCHAR2(25) 
    ,p_size        NUMBER 
    ,p_container   CHAR(10) 
    ,p_retailprice NUMBER 
    ,p_comment     VARCHAR2(23)
);

-----
-- Table: TPCH.H_PARTSUPP
-----
DROP TABLE tpch.h_partsupp PURGE;
CREATE TABLE tpch.h_partsupp (
     ps_partkey    NUMBER NOT NULL 
    ,ps_suppkey    NUMBER NOT NULL 
    ,ps_availqty   NUMBER 
    ,ps_supplycost NUMBER NOT NULL 
    ,ps_comment    VARCHAR2(199)
);

-----
-- Table: TPCH.H_REGION
-----
DROP TABLE tpch.h_region PURGE;
CREATE TABLE tpch.h_region (
     r_regionkey NUMBER 
    ,r_name      CHAR(25) 
    ,r_comment   VARCHAR2(152)
);

-----
-- Table: TPCH.H_SUPPLIER
-----
DROP TABLE tpch.h_supplier PURGE;
CREATE TABLE tpch.h_supplier (
     s_suppkey   NUMBER NOT NULL 
    ,s_name      CHAR(25) 
    ,s_address   VARCHAR2(40) 
    ,s_nationkey NUMBER 
    ,s_phone     CHAR(15) 
    ,s_acctbal   NUMBER 
    ,s_comment   VARCHAR2(101)
);

/* 
|| Listing 4.4:
|| Indexes created during Benchmark Factory�s construction of the TPC-H schema
*/

/*
|| Script:  Create_TPCH_Indexes.sql
|| Purpose: Drops and recreates all indexes in the TPC-H schema
|| Author:  Jim Czuprynski
*/

-----
-- Table: TPCH.H_CUSTOMER
-- Index: TPCH.H_CUSTOMER_IDX1
-----
DROP INDEX tpch.h_customer_idx1;
CREATE UNIQUE INDEX tpch.h_customer_idx1 
  ON tpch.h_customer (c_custkey);

-----
-- Table: TPCH.H_LINEITEM
-- Index: TPCH.H_LINEITEM_IDX1
-----
DROP INDEX tpch.h_lineitem_idx1;
CREATE INDEX tpch.h_lineitem_idx1 
  ON tpch.h_lineitem (l_orderkey);

-----
-- Table: TPCH.H_ORDER
-- Index: TPCH.H_ORDERS_IDX1
-----
DROP INDEX tpch.h_orders_idx1;
CREATE UNIQUE INDEX tpch.h_orders_idx1 
  ON tpch.h_order (o_orderkey);

-----
-- Table: TPCH.H_H_PARTSUPP
-- Index: TPCH.H_PARTSUPP_IDX1
-----
DROP INDEX tpch.h_partsupp_idx1;
CREATE UNIQUE INDEX tpch.h_partsupp_idx1 
  ON tpch.h_partsupp (ps_partkey, ps_suppkey);