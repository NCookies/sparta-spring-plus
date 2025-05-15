# import csv, uuid
# with open('src/test/resources/db/init/users.csv', 'w', newline='') as f:
#     writer = csv.writer(f)
#     writer.writerow(['email','password','nickname','user_role'])
#     for i in range(1_000_000):
#         email = f'user{i:07d}@example.com'
#         nickname = f'nick-{uuid.uuid4()}'
#         writer.writerow([email, 'pass', nickname, 'USER'])

import csv
import uuid
import random
from datetime import datetime, timedelta

NUM = 1_000_000
START = datetime(2020, 1, 1)
END   = datetime(2025, 5, 14)
DELTA = END - START
ROLES = ['USER', 'ADMIN']

with open('users.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    # CSV 헤더: created_at, modified_at, email, password, nickname, user_role
    writer.writerow(['created_at','modified_at','email','password','nickname','user_role'])

    for i in range(NUM):
        created = START + timedelta(days=random.randint(0, DELTA.days),
                                    seconds=random.randint(0, 86400))
        modified = created + timedelta(days=random.randint(0, 90),
                                        seconds=random.randint(0, 86400))
        email = f'user{i:07d}@example.com'
        pwd   = 'pass'
        nick  = f'nick-{uuid.uuid4()}'
        role  = random.choice(ROLES)

        writer.writerow([
            created.strftime('%Y-%m-%d %H:%M:%S'),
            modified.strftime('%Y-%m-%d %H:%M:%S'),
            email,
            pwd,
            nick,
            role
        ])