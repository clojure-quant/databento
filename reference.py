import databento as db
client = db.Reference()

# Define parameters
dataset = "EQUS.MINI"     
start_date = "2023-03-28T00:00:00"  
end_date =   "2026-01-29T22:00:00+00:00" 

defs = client.security_master.get_range(
    countries=["US"],
    symbols="ALL_SYMBOLS",
    #,start=start_date, end=end_date
    end="2026-01-25",
    start="2026-01-20"
    )

# Convert to DataFrame
df_defs = defs.to_df()
df_defs.to_csv("eod-market-defs.csv", index=True)


print(defs.head(10))


# THIS SCRIPT NEEDS LIVE SUBSCRIPTION