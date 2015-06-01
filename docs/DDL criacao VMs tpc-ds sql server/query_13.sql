drop view dbo.v_query_13;
GO

CREATE VIEW [dbo].[v_query_13] WITH SCHEMABINDING 
    AS
select 
COUNT_BIG(*) as id,
c_customer_id as customer_id,
c_last_name + ', ' + c_first_name as customername,  
ib_lower_bound,  
ib_upper_bound,  
c_current_cdemo_sk,  
c_current_hdemo_sk,  
c_current_addr_sk,  
sr_cdemo_sk,  
hd_income_band_sk,  
ca_city 
from 
dbo.customer ,
dbo.customer_address ,
dbo.customer_demographics ,
dbo.household_demographics ,
dbo.income_band ,
dbo.store_returns 
where  c_current_addr_sk = ca_address_sk and 
 ib_income_band_sk = hd_income_band_sk and  
 cd_demo_sk = c_current_cdemo_sk and  
 hd_demo_sk = c_current_hdemo_sk and  
 sr_cdemo_sk = cd_demo_sk 
group by 
c_customer_id,
c_last_name + ', ' + c_first_name,  
ib_lower_bound,  
ib_upper_bound,  
c_current_cdemo_sk,  
c_current_hdemo_sk,  
c_current_addr_sk,  
sr_cdemo_sk,  
hd_income_band_sk,  
ca_city
go

CREATE UNIQUE CLUSTERED INDEX index_v_query_13 ON 
	dbo.v_query_13(customer_id,customername,  
ib_lower_bound,  
ib_upper_bound,  
c_current_cdemo_sk,  
c_current_hdemo_sk,  
c_current_addr_sk,  
sr_cdemo_sk,  
hd_income_band_sk,  
ca_city)
  go


