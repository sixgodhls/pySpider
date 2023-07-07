from motor.motor_asyncio import AsyncIOMotorClient
import asyncio

mongo_connecting = 'mongodb://localhost:27017'
mongo_db_name = 'test'
mongo_collection = 'test'

client = AsyncIOMotorClient(mongo_connecting)
db = client[mongo_db_name]
collection = db[mongo_collection]
i = 0


async def get():
    global i
    i += 1
    return i


async def test():
    print('saving')
    return await collection.update_one({'id': get()}, {'$set': get()}, upsert=True)

async def main():
    tasks = [asyncio.ensure_future(test()) for _ in range(1)]
if __name__ == '__main__':

    asyncio.get_event_loop().run_until_complete(main())

