CREATE SCHEMA agent;

CREATE TABLE agent.tb_access_plan (
  wld_id INTEGER NOT NULL, 
  apl_id_seq INTEGER NOT NULL, 
  apl_text_line VARCHAR(10000), 
  CONSTRAINT pk_tb_access_plan PRIMARY KEY(wld_id, apl_id_seq), 
  CONSTRAINT fk_wld_id FOREIGN KEY (wld_id)
    REFERENCES agent.tb_workload(wld_id)
    MATCH PARTIAL
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    NOT DEFERRABLE
) WITHOUT OIDS;

CREATE TABLE agent.tb_candidate_index (
  cid_id INTEGER NOT NULL, 
  cid_table_name VARCHAR(100) NOT NULL, 
  cid_index_profit INTEGER DEFAULT 0 NOT NULL, 
  cid_creation_cost INTEGER DEFAULT 0 NOT NULL, 
  cid_status CHAR(1), 
  cid_type CHAR(1), 
  cid_initial_profit INTEGER, 
  cid_fragmentation_level INTEGER, 
  CONSTRAINT pk_tb_candidate_index PRIMARY KEY(cid_id)
) WITHOUT OIDS;

CREATE TABLE agent.tb_candidate_index_column (
  cid_id INTEGER NOT NULL, 
  cic_column_name CHAR(100) NOT NULL, 
  cic_type CHAR(100), 
  CONSTRAINT pk_tb_candidate_index_column PRIMARY KEY(cid_id, cic_column_name), 
  CONSTRAINT fk_cid_id FOREIGN KEY (cid_id)
    REFERENCES agent.tb_candidate_index(cid_id)
    MATCH PARTIAL
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    NOT DEFERRABLE
) WITHOUT OIDS;

CREATE TABLE agent.tb_candidate_view (
  cmv_id INTEGER NOT NULL, 
  cmv_ddl_create TEXT NOT NULL, 
  cmv_cost BIGINT, 
  cmv_profit BIGINT NOT NULL, 
  cmv_status CHAR(1) DEFAULT 'H'::bpchar, 
  CONSTRAINT tb_cadidate_view_pkey PRIMARY KEY(cmv_id), 
  CONSTRAINT tb_cadidate_view_fk FOREIGN KEY (cmv_id)
    REFERENCES agent.tb_workload(wld_id)
    MATCH PARTIAL
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) WITHOUT OIDS;

ALTER TABLE agent.tb_candidate_view
  ALTER COLUMN cmv_id SET STATISTICS 0;

ALTER TABLE agent.tb_candidate_view
  ALTER COLUMN cmv_profit SET STATISTICS 0;

COMMENT ON TABLE agent.tb_candidate_view
IS 'Possiveis valores:
H: Hipotetico
R: Real';

COMMENT ON COLUMN agent.tb_candidate_view.cmv_status
IS 'Possiveis valores:
H: Hipotetico
R: Real
M: Materializar';


CREATE TABLE agent.tb_epoque (
  epq_id INTEGER NOT NULL, 
  epq_start INTEGER NOT NULL, 
  epq_end INTEGER NOT NULL, 
  CONSTRAINT pk_tb_epoque PRIMARY KEY(epq_id)
) WITHOUT OIDS;

CREATE TABLE agent.tb_profits (
  cid_id INTEGER NOT NULL, 
  pro_timestamp INTEGER NOT NULL, 
  pro_profit INTEGER NOT NULL, 
  wld_id INTEGER NOT NULL, 
  pro_type CHAR(1), 
  CONSTRAINT pk_tb_profits PRIMARY KEY(cid_id, pro_timestamp), 
  CONSTRAINT fk_wld_id FOREIGN KEY (wld_id)
    REFERENCES agent.tb_workload(wld_id)
    MATCH PARTIAL
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    NOT DEFERRABLE
) WITHOUT OIDS;

CREATE TABLE agent.tb_task_indexes (
  wld_id INTEGER NOT NULL, 
  cid_id INTEGER NOT NULL, 
  CONSTRAINT tb_task_indexes_pkey PRIMARY KEY(wld_id, cid_id), 
  CONSTRAINT tb_task_indexes_cid_id_fkey FOREIGN KEY (cid_id)
    REFERENCES agent.tb_candidate_index(cid_id)
    MATCH PARTIAL
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE, 
  CONSTRAINT tb_task_indexes_wld_id_fkey FOREIGN KEY (wld_id)
    REFERENCES agent.tb_workload(wld_id)
    MATCH PARTIAL
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) WITHOUT OIDS;

CREATE TABLE agent.tb_workload (
  wld_id SERIAL, 
  wld_sql VARCHAR(10000) NOT NULL, 
  wld_plan VARCHAR(10000) NOT NULL, 
  wld_capture_count INTEGER DEFAULT 0 NOT NULL, 
  wld_analyze_count INTEGER DEFAULT 0 NOT NULL, 
  wld_type CHAR(1), 
  wld_relevance INTEGER, 
  CONSTRAINT pk_tb_workload PRIMARY KEY(wld_id)
) WITHOUT OIDS;



