import csv, uuid
with open('src/test/resources/db/init/users.csv', 'w', newline='') as f:
    writer = csv.writer(f)
    writer.writerow(['email','password','nickname','user_role'])
    for i in range(1_000_000):
        email = f'user{i:07d}@example.com'
        nickname = f'nick-{uuid.uuid4()}'
        writer.writerow([email, 'pass', nickname, 'USER'])
