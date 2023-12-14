import sqlite3
from sqlite3 import Error

def create_connection(db_file):
    conn = None
    try:
        path = '../../src/backend/backend/'+db_file
        conn = sqlite3.connect(path)
        return conn
    except Error as e:
        print(e)
