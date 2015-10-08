  SET statement_timeout = 0;
  SET lock_timeout = 0;
  SET client_encoding = 'UTF8';
  SET standard_conforming_strings = on;
  SET check_function_bodies = false;
  SET client_min_messages = warning;



  CREATE SCHEMA agent;


  ALTER SCHEMA agent OWNER TO postgres;

  SET search_path = agent, pg_catalog;



  CREATE FUNCTION limpa_estatisticas() RETURNS boolean
      LANGUAGE sql
      AS $$
  delete from agent.tb_access_plan;
  delete from agent.tb_candidate_view;
  delete from agent.tb_workload;
  delete from agent.tb_task_indexes;
  delete from agent.tb_candidate_index_column;
  delete from agent.tb_candidate_index;
  delete from agent.tb_epoque;
  delete from agent.tb_profits;
  delete from agent.tb_task_indexes;
  select true;
  $$;


  ALTER FUNCTION agent.limpa_estatisticas() OWNER TO postgres;

  SET default_tablespace = '';

  SET default_with_oids = false;



  CREATE TABLE tb_access_plan (
      wld_id integer NOT NULL,
      apl_id_seq integer NOT NULL,
      apl_text_line character varying(10000)
  );


  ALTER TABLE agent.tb_access_plan OWNER TO postgres;



  CREATE TABLE agent.tb_candidate_index (
  cid_id integer NOT NULL,
  cid_table_name character varying(100) NOT NULL,
  cid_index_profit integer NOT NULL DEFAULT 0,
  cid_creation_cost integer NOT NULL DEFAULT 0,
  cid_status character(1),
  cid_type character(1),
  cid_initial_profit integer,
  cid_fragmentation_level integer,
  cid_initial_ratio real,
  cid_index_name character varying(100),
  CONSTRAINT pk_tb_candidate_index PRIMARY KEY (cid_id)
  );


  ALTER TABLE agent.tb_candidate_index OWNER TO postgres;



  CREATE TABLE tb_candidate_index_column (
      cid_id integer NOT NULL,
      cic_column_name character(100) NOT NULL,
      cic_type character(100)
  );


  ALTER TABLE agent.tb_candidate_index_column OWNER TO postgres;



  CREATE TABLE tb_candidate_view (
      cmv_id integer NOT NULL,
      cmv_ddl_create text NOT NULL,
      cmv_cost bigint,
      cmv_profit bigint NOT NULL,
      cmv_status character(1) DEFAULT 'H'::bpchar
  );
  ALTER TABLE ONLY tb_candidate_view ALTER COLUMN cmv_id SET STATISTICS 0;
  ALTER TABLE ONLY tb_candidate_view ALTER COLUMN cmv_profit SET STATISTICS 0;


  ALTER TABLE agent.tb_candidate_view OWNER TO postgres;



  COMMENT ON TABLE tb_candidate_view IS 'Possiveis valores:
  H: Hipotetico
  R: Real';



  COMMENT ON COLUMN tb_candidate_view.cmv_status IS 'Possiveis valores:
  H: Hipotetico
  R: Real
  M: Materializar';



  CREATE TABLE tb_epoque (
      epq_id integer NOT NULL,
      epq_start integer NOT NULL,
      epq_end integer NOT NULL
  );


  ALTER TABLE agent.tb_epoque OWNER TO postgres;



  CREATE TABLE tb_profits (
      cid_id integer NOT NULL,
      pro_timestamp integer NOT NULL,
      pro_profit integer NOT NULL,
      wld_id integer NOT NULL,
      pro_type character(1)
  );


  ALTER TABLE agent.tb_profits OWNER TO postgres;



  CREATE TABLE tb_task_indexes (
      wld_id integer NOT NULL,
      cid_id integer NOT NULL
  );


  ALTER TABLE agent.tb_task_indexes OWNER TO postgres;


CREATE TABLE agent.tb_workload_log
(
  wlog_sql text NOT NULL,
  wlog_plan text NOT NULL,
  wlog_time timestamp with time zone NOT NULL,
  wlog_id serial NOT NULL,
  wlog_type character(1),
  CONSTRAINT wlog_pk PRIMARY KEY (wlog_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE agent.tb_workload_log
  OWNER TO postgres;


  CREATE TABLE tb_workload (
      wld_id integer NOT NULL,
      wld_sql character varying(10000) NOT NULL,
      wld_plan character varying(10000) NOT NULL,
      wld_capture_count integer DEFAULT 0 NOT NULL,
      wld_analyze_count integer DEFAULT 0 NOT NULL,
      wld_type character(1),
      wld_relevance integer
  );


  ALTER TABLE agent.tb_workload OWNER TO postgres;



  CREATE SEQUENCE tb_workload_wld_id_seq
      START WITH 1
      INCREMENT BY 1
      NO MINVALUE
      NO MAXVALUE
      CACHE 1;


  ALTER TABLE agent.tb_workload_wld_id_seq OWNER TO postgres;



  ALTER SEQUENCE tb_workload_wld_id_seq OWNED BY tb_workload.wld_id;


  ALTER TABLE ONLY tb_workload ALTER COLUMN wld_id SET DEFAULT nextval('tb_workload_wld_id_seq'::regclass);




  ALTER TABLE ONLY tb_access_plan
      ADD CONSTRAINT pk_tb_access_plan PRIMARY KEY (wld_id, apl_id_seq);



  ALTER TABLE ONLY tb_candidate_index
      ADD CONSTRAINT pk_tb_candidate_index PRIMARY KEY (cid_id);



  ALTER TABLE ONLY tb_candidate_index_column
      ADD CONSTRAINT pk_tb_candidate_index_column PRIMARY KEY (cid_id, cic_column_name);



  ALTER TABLE ONLY tb_epoque
      ADD CONSTRAINT pk_tb_epoque PRIMARY KEY (epq_id);




  ALTER TABLE ONLY tb_profits
      ADD CONSTRAINT pk_tb_profits PRIMARY KEY (cid_id, pro_timestamp);




  ALTER TABLE ONLY tb_workload
      ADD CONSTRAINT pk_tb_workload PRIMARY KEY (wld_id);




  ALTER TABLE ONLY tb_candidate_view
      ADD CONSTRAINT tb_cadidate_view_pkey PRIMARY KEY (cmv_id);




  ALTER TABLE ONLY tb_task_indexes
      ADD CONSTRAINT tb_task_indexes_pkey PRIMARY KEY (wld_id, cid_id);




  ALTER TABLE ONLY tb_candidate_index_column
      ADD CONSTRAINT fk_cid_id FOREIGN KEY (cid_id) REFERENCES tb_candidate_index(cid_id);



  ALTER TABLE ONLY tb_access_plan
      ADD CONSTRAINT fk_wld_id FOREIGN KEY (wld_id) REFERENCES tb_workload(wld_id);




  ALTER TABLE ONLY tb_profits
      ADD CONSTRAINT fk_wld_id FOREIGN KEY (wld_id) REFERENCES tb_workload(wld_id);




  ALTER TABLE ONLY tb_candidate_view
      ADD CONSTRAINT tb_cadidate_view_fk FOREIGN KEY (cmv_id) REFERENCES tb_workload(wld_id) ON UPDATE CASCADE ON DELETE CASCADE;



  ALTER TABLE ONLY tb_task_indexes
      ADD CONSTRAINT tb_task_indexes_cid_id_fkey FOREIGN KEY (cid_id) REFERENCES tb_candidate_index(cid_id) ON UPDATE CASCADE ON DELETE CASCADE;



  ALTER TABLE ONLY tb_task_indexes
      ADD CONSTRAINT tb_task_indexes_wld_id_fkey FOREIGN KEY (wld_id) REFERENCES tb_workload(wld_id) ON UPDATE CASCADE ON DELETE CASCADE;

