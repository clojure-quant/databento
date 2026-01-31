import databento as db

# Create the historical client
client = db.Historical()

# Define parameters
dataset = "EQUS.MINI"     
start_date = "2023-03-28T00:00:00"  
end_date =   "2026-01-29T22:00:00+00:00" 

# Download the data
data_store = client.symbology.resolve(
    dataset=dataset,
    #symbols="ALL_SYMBOLS",
    symbols=["9861", "9771"],
    stype_in='instrument_id',
    stype_out='raw_symbol',
    start_date=start_date
    
)

print(data_store)

#{'result': {'9861': [{'d0': '2023-03-28', 'd1': '2023-03-29', 's': 'LIFE'}], 
#            '9771': [{'d0': '2023-03-28', 'd1': '2023-03-29', 's': 'LEXXW'}]},
# 'symbols': ['9861', '9771'], 
# 'stype_in': 'instrument_id', 
# 'stype_out': 'raw_symbol', 'start_date': '2023-03-28', 
# 'end_date': '2023-03-29', 'partial': [], 'not_found': [], 
# 'message': 'OK', 'status': 0}
