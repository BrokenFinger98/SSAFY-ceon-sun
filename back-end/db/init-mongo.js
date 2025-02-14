// init-mongo.js
print("🔹 MongoDB 초기 사용자 설정 시작...");

// chatdb를 사용하여 사용자 생성
db = db.getSiblingDB("chatdb");
db.createUser({
    user: "chunsun",
    pwd: "sixman1!",
    roles: [{ role: "readWrite", db: "chatdb" }]
});

print("✅ chatuser 계정 생성 완료!");
