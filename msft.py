import databento as db

# Create the historical client
client = db.Historical()

# Define parameters
dataset = "EQUS.MINI"      # Example dataset (CME Globex futures)
schema = "tbbo"     # Top-of-book schema
symbols = ["MSFT"]       # Change to the symbol(s) you want
start_date = "2026-01-01"  # Beginning of your month
end_date = "2026-01-26T23:59:59"  # End of month

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

df.to_csv("msft_jan2025.csv", index=False)

print(df.head())

