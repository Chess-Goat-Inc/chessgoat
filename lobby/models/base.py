from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import MetaData

target_metadata = MetaData()
Base = declarative_base(metadata=target_metadata)