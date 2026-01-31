import databento as db
client = db.Historical()

# Define parameters
dataset = "EQUS.MINI"     
start_date = "2023-03-28T00:00:00"  
end_date =   "2026-01-29T22:00:00+00:00" 

mapping = client.symbology.resolve(
    dataset=dataset,
    symbols="ALL_SYMBOLS",
    #symbols=["9861", "9771"],
    stype_in='instrument_id',
    stype_out='raw_symbol',
    start_date=start_date)

mapping = mapping["result"]

print("mapping")
print(mapping)

# {'9861': [{'d0': '2023-03-28', 'd1': '2023-03-29', 's': 'LIFE'}], '9771': [{'d0': '2023-03-28', 'd1': '2023-03-29', 's': 'LEXXW'}]}


# Download the data
data_store = client.timeseries.get_range(
    dataset=dataset,
    schema="ohlcv-1d",
    symbols="ALL_SYMBOLS",
    #symbols=["9861", "9771"],
    stype_in='instrument_id',
    #stype_in='raw_symbol',
    stype_out='raw_symbol',
    start=start_date,
    end=end_date,
)

# Convert to a pandas DataFrame
df = data_store.to_df()
print("head of original bars")
print(df.head())
print(df.dtypes)
df.to_csv("eod-market-nomapping.csv", index=True)



def id_to_symbol(asset_id):
    """
    Return raw_symbol for a given instrument_id.
    Returns 'XXX' if not found.
    """
    try:
        #r = mapping.get(int(asset_id))
        r = mapping.get(str(asset_id))
        if not r:
          return "XXX"
        else:
          return r[0]["s"]  

    except (TypeError, ValueError):
        return "XXX"


df["symbol2"] = df["instrument_id"].map(id_to_symbol)

df.to_csv("eod-market-mapped.csv", index=True)

print(df.head())




