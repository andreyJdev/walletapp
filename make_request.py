import requests
import threading

url = "http://localhost:8081/api/v1/wallet"
data = {
    "walletId": "427ec1f5-f229-4782-83ad-50007f4bc3c2",
    "operationType": "DEPOSIT",
    "amount": 100
}

def send_requests():
    for i in range(1000):
        response = requests.post(url, json=data)
        print(f"Request {i+1}: Status Code {response.status_code}")

threads = []

for _ in range(10):
    thread = threading.Thread(target=send_requests)
    threads.append(thread)
    thread.start()

for thread in threads:
    thread.join()