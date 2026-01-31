import databento as db

# Create the historical client
client = db.Historical()

# Define parameters
dataset = "EQUS.MINI"      # Example dataset (CME Globex futures)
schema = "tbbo"     # Top-of-book schema
symbols = ["NVDA", "BBAI", "PLUG","RIVN","TSLA","INTC", "AMD", "AAPL",
    "CMCSA", "META", "MU","COIN", "SOFI","HOOD","EVTV", "ONDS", "XTKG",
    "AIIO",  "DRCT",  "WORX"]  # Change to the symbol(s) you want
start_date = "2026-01-27T00:00:00"  # Beginning of your month
end_date =   "2026-01-28T22:00:00+00:00" # End of month


# Download the data
data_store = client.timeseries.get_range(
    dataset=dataset,
    schema=schema,
    symbols=symbols,
    start=start_date,
    end=end_date,
)

# Convert to a pandas DataFrame
df = data_store.to_df()

df.to_csv("market_jan_27_28.csv", index=False)

print(df.head())

