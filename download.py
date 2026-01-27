import databento as db

# Create the historical client
client = db.Historical()

# Define parameters
dataset = "GLBX.MDP3"      # Example dataset (CME Globex futures)
schema = "tbbo"     # Top-of-book schema
symbols = ["ES.FUT"]       # Change to the symbol(s) you want
start_date = "2026-01-01"  # Beginning of your month
end_date = "2026-01-26T23:59:59"  # End of month

# Download the data
data_store = client.timeseries.get_range(
    dataset=dataset,
    schema=schema,
    symbols=symbols,
    stype_in="parent",
    start=start_date,
    end=end_date,
)

# Convert to a pandas DataFrame
df = data_store.to_df()
print(df.head())

# Optionally save to a CSV
df.to_csv("top_of_book_jan2025.csv", index=False)
