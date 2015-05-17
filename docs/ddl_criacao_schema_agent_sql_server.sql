CREATE SCHEMA agent;
GO

CREATE TABLE [agent].[tb_candidate_view](
	[cmv_id] [int] NOT NULL,
	[cmv_ddl_create] [text] NOT NULL,
	[cmv_cost] [bigint] NULL,
	[cmv_profit] [bigint] NOT NULL,
	[cmv_status] [char](1) NULL,
 CONSTRAINT [PK_tb_candidate_view] PRIMARY KEY CLUSTERED 
(
	[cmv_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

CREATE TABLE [agent].[tb_workload](
	[wld_id] [int] IDENTITY(1,1) NOT NULL,
	[wld_sql] [text] NOT NULL,
	[wld_plan] [text] NOT NULL,
	[wld_capture_count] [int] NOT NULL,
	[wld_analyze_count] [int] NOT NULL,
	[wld_type] [char](1) NULL,
	[wld_relevance] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[wld_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO


CREATE PROCEDURE limpar_agent_dados 
AS
BEGIN
    -- Insert statements for procedure here
	DELETE FROM [sql_server_tpch_5gb].[agent].[tb_workload]
	DELETE FROM [sql_server_tpch_5gb].[agent].[tb_candidate_view]
	RETURN 1
END
GO
