# MasterProjektBackend
This is the backend to merge data from http Archive and Google BigQuery

## Installation
### Postgres DB
Install postgressql. We are running version 13.3-1
Install pgAdmin4. Create a Database named test with the password: test123 TODO: Adjust this comment for shipping.

### Python Requirements
We are running python 3.9.1
First of you have to install all the necessary requirements via this command: 
```
pip install -r requirements.txt
```

### Init DB
Open a Terminal and run: 
```
python models.py
``` to create the corresponding database Table.

### Start the application
Make sure you have finished all previouse steps successfully.
Open a Terminal and run: 
```
python app.py
``` 