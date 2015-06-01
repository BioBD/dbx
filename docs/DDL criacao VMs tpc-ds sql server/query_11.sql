DROP VIEW [dbo].[v_query_11];
GO


CREATE VIEW [dbo].[v_query_11] WITH SCHEMABINDING 
    AS
select s_store_name, 
s_store_id, 
sun_sales, 
mon_sales, 
tue_sales, 
wed_sales, 
thu_sales, 
fri_sales, 
sat_sales,  
d_year, 
 s_gmt_offset, 
  ss_store_sk from (
select 
 COUNT_BIG(*) ID,
s_store_name, 
s_store_id, 
sum(case when (d_day_name='sunday') then ss_sales_price else null end) sun_sales, 
sum(case when (d_day_name='monday') then ss_sales_price else null end) mon_sales, 
sum(case when (d_day_name='tuesday') then ss_sales_price else null end) tue_sales, 
sum(case when (d_day_name='wednesday') then ss_sales_price else null end) wed_sales, 
sum(case when (d_day_name='thursday') then ss_sales_price else null end) thu_sales, 
sum(case when (d_day_name='friday') then ss_sales_price else null end) fri_sales, 
sum(case when (d_day_name='saturday') then ss_sales_price else null end) sat_sales,  
d_year, 
 s_gmt_offset, 
  ss_store_sk 
  from 
  dbo.date_dim, 
  dbo.store_sales, 
  dbo.store 
  where  
  d_date_sk = ss_sold_date_sk and 
  s_store_sk = ss_store_sk 
  group by 
  s_store_name, 
s_store_id, 
d_year, 
 s_gmt_offset, 
  ss_store_sk
  
  ) as q
  group by 
s_store_name, 
s_store_id, 
sun_sales, 
mon_sales, 
tue_sales, 
wed_sales, 
thu_sales, 
fri_sales, 
sat_sales,  
d_year, 
 s_gmt_offset, 
  ss_store_sk
 GO


CREATE UNIQUE CLUSTERED INDEX index_v_query_11 ON 
	dbo.v_query_11(
s_store_name, 
s_store_id, 
sun_sales, 
mon_sales, 
tue_sales, 
wed_sales, 
thu_sales, 
fri_sales, 
sat_sales,  
d_year, 
 s_gmt_offset, 
  ss_store_sk)
  go


